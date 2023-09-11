package com.web.mqtt;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.iot.mqtt.IotDevice;
import com.web.common.iot.mqtt.IotDeviceLog;
import com.web.mqtt.mapper.IotDeviceMapper;
import com.web.mqtt.service.impl.IotDeviceLogServiceImpl;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

@Service
public class MqttConnect {
	
	private static final String MQTT_REMOTE_KEY = "IOT::MQTT::ATTEMPT::";
	
	/** MQTT创建连接最大尝试次数（3次） */
	private static final int MQTT_ATTEMPT_MAX_TIMES = 3;
	
	@Autowired
	private IotDeviceMapper iotDeviceMapper;
	
	@Autowired
	private IotDeviceLogServiceImpl iotDeviceLogServiceImpl;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private MqttClientManage mqttClientManage;
	
	/**
	 * 客户端请求连接时的处理。<br>
	 * 1、根据clientId，userName和password判断设备信息是否正确；<br>
	 * 2、认证通过则返回成功信息，否则返回认证失败信息。<br>
	 * @param mqttMsg 消息信息
	 * @param fixedHeader 固定头
	 * @return MqttMessage 返回的消息内容
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public MqttMessage setConnAckMessage(Channel channel, MqttMessage mqttMsg, MqttFixedHeader fixedHeader) {
		//获取请求的连接的IP地址
		String ip = getRemote(channel);
		if(!StringUtils.hasText(ip)) {
			return refuseConnect(fixedHeader, MqttConnectReturnCode.CONNECTION_REFUSED_BANNED);
		}
		//尝试次数过多直接返回拒绝连接
		String attempts = redisTemplate.opsForValue().get(MQTT_REMOTE_KEY + ip);
		if(StringUtils.hasLength(attempts) && Integer.parseInt(attempts) > MQTT_ATTEMPT_MAX_TIMES) {
			return refuseConnect(fixedHeader, MqttConnectReturnCode.CONNECTION_REFUSED_BANNED);
		}
		
		MqttConnectMessage connectMessage = (MqttConnectMessage) mqttMsg;
		MqttConnectVariableHeader connectVariableHeader = connectMessage.variableHeader();
		MqttConnectPayload payload = connectMessage.payload();
		String clientId = payload.clientIdentifier();
		String userName = payload.userName();
		byte[] passwordInBytes = payload.passwordInBytes();
		
		if(!StringUtils.hasText(clientId) || !StringUtils.hasText(userName) || passwordInBytes == null) {
			saveAttemptConnectTimes(ip);
			return refuseConnect(fixedHeader, MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
		}
		//判断认证信息是否正确
		LambdaQueryWrapper<IotDevice> deviceQuery = new LambdaQueryWrapper<>();
		deviceQuery.eq(IotDevice::getDeviceName, clientId);
		deviceQuery.eq(IotDevice::getDeviceKey, userName);
		deviceQuery.eq(IotDevice::getDeviceSecret, new String(passwordInBytes));
		List<IotDevice> deviceList = iotDeviceMapper.selectList(deviceQuery);
		if(deviceList == null || deviceList.isEmpty()) {
			saveAttemptConnectTimes(ip);
			return refuseConnect(fixedHeader, MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
		}
		Integer deviceId = deviceList.get(0).getDeviceId();
		updateDeviceStatus(deviceId, DataSourcesSymbol.DEVICE_STATUS_2);
 		
 		mqttClientManage.updateClientChannel(clientId, channel, 0);
		
 		//清除尝试次数
 		redisTemplate.delete(MQTT_REMOTE_KEY + ip);
 		
 		MqttFixedHeader ackFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, fixedHeader.isDup(), MqttQoS.AT_MOST_ONCE, fixedHeader.isRetain(), 0x00);
		MqttConnAckVariableHeader ackVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, connectVariableHeader.isCleanSession());
		return new MqttConnAckMessage(ackFixedHeader, ackVariableHeader);
	}
	
	/**
	 * 设置拒绝连接
	 * @param fixedHeader 传输的固定header
	 * @param returnCode 返回码
	 * @return MqttConnAckMessage 连接的应答消息
	 *
	 * @author zhouhui
	 * @since 2023.06.15 
	 */
	private MqttConnAckMessage refuseConnect(MqttFixedHeader fixedHeader, MqttConnectReturnCode returnCode) {
		MqttFixedHeader ackFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, fixedHeader.isDup(), MqttQoS.AT_MOST_ONCE, fixedHeader.isRetain(), 0x00);
		MqttConnAckVariableHeader ackVariableHeader = new MqttConnAckVariableHeader(returnCode, true);
		return new MqttConnAckMessage(ackFixedHeader, ackVariableHeader);
	}
	
	/**
	 * 记录尝试连接的次数
	 * @param ip 远程访问地址
	 *
	 * @author zhouhui
	 * @since 2023.06.15 
	 */
	private void saveAttemptConnectTimes(String ip) {
		int times = 0;
		String attempts = redisTemplate.opsForValue().get(MQTT_REMOTE_KEY + ip);
		if(StringUtils.hasText(attempts)) {
			times = Integer.parseInt(attempts);
		}
		times += 1;
		redisTemplate.opsForValue().set(MQTT_REMOTE_KEY + ip, times + "", 30, TimeUnit.MINUTES);
	}
	
	/**
	 * 获取远程调用信息
	 * @param channel 
	 * @return String IP地址
	 *
	 * @author zhouhui
	 * @since 2023.06.15 
	 */
	private String getRemote(Channel channel) {
		SocketAddress remote = channel.remoteAddress();
		if(remote instanceof InetSocketAddress) {
			InetSocketAddress inetSocket = (InetSocketAddress) remote;
			InetAddress addr = inetSocket.getAddress();
			if(addr != null) {
				return addr.getHostAddress();
			}
		}
		return null;
	}
	
	/**
	 * 断开连接。
	 * @param channel 当前的连接
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public void setDisconnect(Channel channel) {
		String channelId = channel.id().asLongText();
		String clientId = mqttClientManage.getClientIdByChannelId(channelId);
		if(!StringUtils.hasText(clientId)) {
			return;
		}
		
		LambdaQueryWrapper<IotDevice> deviceQuery = new LambdaQueryWrapper<>();
		deviceQuery.eq(IotDevice::getDeviceName, clientId);
		List<IotDevice> deviceList = iotDeviceMapper.selectList(deviceQuery);
		if(deviceList == null || deviceList.isEmpty()) {
			return;
		}
		Integer deviceId = deviceList.get(0).getDeviceId();
		updateDeviceStatus(deviceId, DataSourcesSymbol.DEVICE_STATUS_1);
		
		mqttClientManage.updateClientChannel(null, channel, 1);
	}
	
	/**
	 * 设置心跳返回信息。
	 * @return MqttMessage 信息内容
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public MqttMessage setPingReqMsg(Channel channel) {
		//更新连接活跃时间
		mqttClientManage.updateChannelDatetime(channel);
		
		MqttFixedHeader ackFixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, MqttConnectReturnCode.CONNECTION_ACCEPTED.byteValue());
		MqttConnAckVariableHeader ackVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
		return new MqttConnAckMessage(ackFixedHeader, ackVariableHeader);
	}
	
	/**
	 * 更新设备的状态，并记录日志
	 * @param deviceId 设备的Id
	 * @param status 设备的状态
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private void updateDeviceStatus(Integer deviceId, Integer status) {
		// 更新设备的状态
		LambdaUpdateWrapper<IotDevice> deviceUpdate = new LambdaUpdateWrapper<>();
		deviceUpdate.eq(IotDevice::getDeviceId, deviceId);
		deviceUpdate.set(IotDevice::getDeviceStatus, status);
		iotDeviceMapper.update(null, deviceUpdate);

		// 添加日志
		IotDeviceLog deviceLog = new IotDeviceLog();
		deviceLog.setDeviceId(deviceId);
		deviceLog.setDeviceStatus(status);
		iotDeviceLogServiceImpl.saveLog(deviceLog);
	}
}
