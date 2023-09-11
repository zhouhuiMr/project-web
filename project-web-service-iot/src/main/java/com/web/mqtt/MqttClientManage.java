package com.web.mqtt;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.netty.channel.Channel;

/**
 * 客户端只能连接到单独的服务器，如果要搭建负载，需要另外考虑。<br>
 * 1、防止同一个设备连接上多台服务器；<br>
 * 2、如何实现负载。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class MqttClientManage {

	/** 设备clientId对应管道 */
	private static final Map<String, Channel> CLIENT_CHANNEL = new HashMap<>();
	
	/** 管道的ID对应设备clientId */
	private static final Map<String, String> CHANNELID_CLIENT = new HashMap<>();
	
	/** 管道的ID对应连接时间 */
	private static final Map<String, LocalDateTime> CHANNELID_UP_TIME = new HashMap<>();
	
	/**
	 * 更新客户端对应的连接信息
	 * @param clientId 客户端id
	 * @param channel 连接信息
	 * @param type 0新增；1删除
	 *
	 * @author zhouhui
	 * @since 1.0.0 
	 */
	public synchronized boolean updateClientChannel(String clientId, Channel channel, int type) {
		boolean isRight = false;
		if(type == 0) {
			//新增
			isRight = saveClientChannel(clientId, channel);
		}else {
			//删除
			isRight = removeClientChannel(channel);
		}
		return isRight;
	}
	
	/**
	 * 保存clientId和channel的对应关系。<br>
	 * 1、如果设备和连接的对应关系已经存在，先断开已有连接，删除对应的关系；<br>
	 * 2、增加新的设备与连接的对应关系。
	 * @param clientId 设备信息
	 * @param channel 连接的管道
	 * @return boolean true 成功；false失败
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private boolean saveClientChannel(String clientId, Channel channel) {
		if(!StringUtils.hasText(clientId) || channel == null) {
			return false;
		}
		Channel saveChannel = CLIENT_CHANNEL.get(clientId);
		String channelId = channel.id().asLongText();
		if(saveChannel != null) {
			try {
				saveChannel.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String saveChannelId = saveChannel.id().asLongText();
			CHANNELID_CLIENT.remove(saveChannelId);
			CHANNELID_UP_TIME.remove(saveChannelId);
		}
		
		//添加新的对应关系
		CLIENT_CHANNEL.put(clientId, channel);
		CHANNELID_CLIENT.put(channelId, clientId);
		CHANNELID_UP_TIME.put(channelId, LocalDateTime.now());
		
		return true;
	}
	
	/**
	 * 删除设备对应连接的对应关系。<br>
	 * 1、断开连接；<br>
	 * 2、删除设备与连接的对应关系。
	 * 
	 * @param clientId 设备信息
	 * @param channel 连接管道
	 * @return true 成功；false失败
	 *
	 * @author zhouhui
	 * @since 1.0.0 
	 */
	private boolean removeClientChannel(Channel channel) {
		if(channel == null) {
			return false;
		}
		
		try {
			channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String channelId = channel.id().asLongText();
		String clientId = CHANNELID_CLIENT.get(channelId);
		if(StringUtils.hasText(clientId)) {
			CLIENT_CHANNEL.remove(clientId);
		}
		
		CHANNELID_CLIENT.remove(channelId);
		CHANNELID_UP_TIME.remove(channelId);
		
		return true;
	}
	
	/**
	 * 更新连接的活跃时间
	 * @param channel 连接信息
	 *
	 * @author zhouhui
	 * @since 2023.06.09 
	 */
	public void updateChannelDatetime(Channel channel) {
		if(channel == null) {
			return;
		}
		String channelId = channel.id().asLongText();
		if(!StringUtils.hasText(channelId)) {
			return;
		}
		CHANNELID_UP_TIME.computeIfPresent(channelId, (key, value) -> {
			CHANNELID_UP_TIME.put(channelId, LocalDateTime.now());
			return value;
		});
	}
	
	/**
	 * 根据连接获取设备信息
	 * @param channelId 连接Id
	 * @return String 设备信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public String getClientIdByChannelId(String channelId) {
		return CHANNELID_CLIENT.get(channelId);
	}
	
	/**
	 * 清除超时的客户端信息，每10分钟执行一次。
	 * 当前时间减10钟，还在存储时间之后则认为连接失效，系统会清理存储的缓存。
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void cleanTimeoutClient() {
		LocalDateTime curTime = LocalDateTime.now();
		if(!CHANNELID_UP_TIME.isEmpty()) {
			Set<String> keySet = CHANNELID_UP_TIME.keySet();
			keySet.forEach(key -> {
				LocalDateTime saveTime = CHANNELID_UP_TIME.get(key);
				if(saveTime != null && curTime.minusMinutes(10L).isAfter(saveTime)) {
					String clientId = CHANNELID_CLIENT.get(key);
					Channel channel = CLIENT_CHANNEL.get(clientId);
					updateClientChannel(clientId, channel, 1);
				}
			});
		}
	}
}
