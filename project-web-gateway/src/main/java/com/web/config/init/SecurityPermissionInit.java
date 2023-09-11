package com.web.config.init;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.web.common.security.AuthenticationEntity;
import com.web.common.user.entity.WebUserEntity;

/**
 * 暂时不使用feign进行远程调用，
 * 因为配置feign时启动报错。
 * 
 * @since 2021.07.02
 * @author zhouhui
 */
@Component
public class SecurityPermissionInit{
	
	private static final Logger log = LoggerFactory.getLogger(SecurityPermissionInit.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	
	/**
	 * 根据token获取登录用户信息，以及对应的角色列表信息
	 * 是否允许访问当前请求的地址
	 * 数据结构
	 * {
	 *   roleId : {
	 *     requestUrl : isAllow
	 *   }
	 * }
	 * isAllow：0、不允许访问；1、允许访问。
	 * @param requestPath 访问的请求地址
	 * @return boolean 允许返回true，不允许返回false
	 * 
	 * @author zhouhui
	 * @since 2021.03.23
	 */
	public boolean isAllow(String requestPath,WebUserEntity webUser) {
		boolean isAllow = false;
		
		LocalDateTime runStart = LocalDateTime.now();
		ArrayList<Integer> roleList = webUser.getRoles();
		if(roleList != null) {
			for(int i=0; i<roleList.size(); i++) {
				Integer roleId = roleList.get(i);
				String roleKey = AuthenticationEntity.getRoleKey(roleId.toString());
				Object pathCode = redisTemplate.opsForHash().get(roleKey, requestPath);
				if(pathCode == null) {
					continue;
				}
				if(AuthenticationEntity.ALLOW_ACCESS.equals(pathCode.toString())) {
					isAllow = true;
				}
			}
		}
		LocalDateTime runEnd = LocalDateTime.now();
		long interval = Duration.between(runStart, runEnd).toMillis();
		String isAllowStr = "";
		if(isAllow) {
			isAllowStr = "允许访问";
		}else {
			isAllowStr = "禁止访问";
		}
		log.info("[用户id:{}][{}][{}]，耗时{}ms",webUser.getUserId(),requestPath,isAllowStr,interval);
		return isAllow;
	}
}
