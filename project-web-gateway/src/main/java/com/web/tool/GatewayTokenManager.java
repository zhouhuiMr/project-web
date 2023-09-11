package com.web.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.web.common.properties.TokenConfigEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.security.AuthenticationEntity;
import com.web.common.token.JWTTokenManager;
import com.web.common.user.entity.WebUserEntity;


@Component
public class GatewayTokenManager {
	
	@Autowired
	private TokenConfigEntity tokenConfig = null;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**   生成token的管理类     */
	private JWTTokenManager tokenManager = new JWTTokenManager();
	
	
	/**
	 * 根据token获取用户的对应信息
	 * @param token 获取信息的token字符串
	 * @return ResultJson<WebUser> 获取信息的处理结果
	 * 
	 * @author zhouhui
	 * @since 2021.01.21
	 */
	@SuppressWarnings("unchecked")
	public R<WebUserEntity> getUserFromToken(String token){
		R<WebUserEntity> json = new R<>();
		
		HashMap<String, String> keyNameMap = new HashMap<>();
		keyNameMap.put("userId","Integer");
		keyNameMap.put("code","String");
		keyNameMap.put("userType", "String");
		keyNameMap.put("roles","List");
		
		R<HashMap<String, Object>> result = 
				tokenManager.getDataFromToken(token, tokenConfig.getTokenKey(), tokenConfig.getTokenSignature(), tokenConfig.getTokenExpire(), keyNameMap);
		if(!ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
			json.setCode(result.getCode());
			json.setMessage(result.getMessage());
			return json;
		}
		HashMap<String, Object> dataMap = result.getData();
		WebUserEntity user = new WebUserEntity();
		user.setUserId(Integer.valueOf(dataMap.get("userId").toString()));
		user.setCode(dataMap.get("code").toString());
		user.setRoles((ArrayList<Integer>)dataMap.get("roles"));
		user.setUserType(dataMap.get("userType").toString());
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(user);
		
		return json;
	}
	
	/**
	 * 根据请求head中的“Login-Token”获取token值。
	 * JWTAuthenticationFilter 已经校验有效性。
	 * @param request 请求
	 * @return ResultJson<WebUser> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2021.03.25
	 */
	public WebUserEntity getUserFromRequest(ServerHttpRequest request){
		String token = null;
		List<String> headList = request.getHeaders().get(tokenConfig.getHeadTokenName());
		if(headList == null || headList.size() != 1) {
			return null;
		}
		token = headList.get(0);
		R<WebUserEntity> json = getUserFromToken(token);
		if(json == null) {
			return null;
		}
		return json.getData();
	}
	
	/**
	 * 判断redis中存储的token和传输的token是否一致，
	 * 不一致说明再次登录过。
	 * @param userId 用户的唯一标识
	 * @param token 校验信息
	 * @return boolean true校验通过，false校验失败
	 *
	 * @author zhouhui
	 * @since 2021.12.04
	 */
	public boolean singleLogin(Integer userId,String token) {
		String redistUserId = AuthenticationEntity.USER_PREFIX + userId;
		Object temp = redisTemplate.opsForHash().get(redistUserId, AuthenticationEntity.USER_KEY_TOKEN);
		if(temp == null) {
			return false;
		}
		String storeToke = temp.toString();
		return token.equals(storeToke);
	}
	
}
