package com.web.user.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.web.common.event.RoleRefreshEvent;
import com.web.common.properties.LockKeyEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.security.AuthenticationEntity;
import com.web.user.service.WebRoleRequestService;

/**
 * 基于Spring的事件驱动。
 * 获取角色对应的接口数据，并将数据存储到redis中。<p>
 * 注：可能存在同时启动多个项目，此时就有可能存在并发问题，添加分布式锁进行控制，防止数据异常。<p>
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class RoleRefreshListener implements ApplicationListener<RoleRefreshEvent>{
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private WebRoleRequestService webRoleRequestServiceImpl;
	
	@Autowired
	private RedissonClient redissonClient;

	@Override
	public void onApplicationEvent(RoleRefreshEvent event) {
		RLock lock = redissonClient.getLock(LockKeyEntity.INIT_ROLE_LOCK);
		if(lock.isLocked() && !event.isWait()) {
			return;
		}
		try {
			lock.lock();
			List<Integer> roleIdList = new ArrayList<>();
			if(event.getRoleId() != null) {
				roleIdList.add(event.getRoleId());
			}
			R<HashMap<String, HashMap<String, String>>> result = webRoleRequestServiceImpl.getAllowRequest(roleIdList);
			if(!ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
				return;
			}
			HashMap<String, HashMap<String, String>> dataMap = result.getData();
			if(dataMap.isEmpty()) {
				return;
			}
			//清空角色对应的接口信息
			String redisKey = "";
			if(event.getRoleId() != null) {
				redisKey = AuthenticationEntity.ROLE_PREFIX + event.getRoleId().intValue();
			}else {
				redisKey = AuthenticationEntity.ROLE_PREFIX.replace(":", "") + "*";
			}
			Set<String> keys = redisTemplate.keys(redisKey);
			if(keys != null && !keys.isEmpty()) {
				redisTemplate.delete(keys);
			}
			
			Iterator<String> keyIter = dataMap.keySet().iterator();
			while(keyIter.hasNext()) {
				String key = keyIter.next();
				HashMap<String, String> mapItem = dataMap.get(key);
				if(mapItem != null && !mapItem.isEmpty()) {
					String storeKey = AuthenticationEntity.getRoleKey(key);
					redisTemplate.opsForHash().putAll(storeKey, mapItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}

}
