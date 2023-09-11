package com.web.service.tool;

import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.web.common.properties.TokenConfigEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.token.JWTTokenManager;
import com.web.common.user.entity.WebUser;
import com.web.common.user.entity.WebUserEntity;


@Component
public class WebUserTokenManager {
	
	/** userId */
	public static final String USER_ID_NAME = "userId";
	
	/** code */
	public static final String CODE_NAME = "code";
	
	/** roles */
	public static final String ROLES_NAME = "roles";
	
	/** userType */
	public static final String USER_TYPE_NAME = "userType";
	
	@Autowired
	private TokenConfigEntity tokenConfig;

	/**   生成token的管理类     */
	private JWTTokenManager tokenManager = new JWTTokenManager();
	
	
	/**
	 * 根据数据生成对应的token
	 * @param user 用户信息
	 * @return String 生成的token字符串
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public String createToken(WebUserEntity user) {
		HashMap<String, Object> option = new HashMap<>();
		option.put(USER_ID_NAME, user.getUserId());
		option.put(CODE_NAME, user.getCode());
		option.put(ROLES_NAME, user.getRoles());
		option.put(USER_TYPE_NAME, user.getUserType());
		return tokenManager.createToken(option, tokenConfig.getTokenKey(), tokenConfig.getTokenSignature(), tokenConfig.getTokenExpire());
	}
	
	/**
	 * 根据token获取用户的对应信息
	 * @param token 获取信息的token字符串
	 * @return ResultJson<WebUser> 获取信息的处理结果
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public R<WebUser> getUserFromToken(String token){
		R<WebUser> json = new R<>();
		
		HashMap<String, String> keyNameMap = new HashMap<>();
		keyNameMap.put(USER_ID_NAME,"Integer");
		keyNameMap.put(CODE_NAME,"String");
		keyNameMap.put(USER_TYPE_NAME, "String");
		keyNameMap.put(ROLES_NAME,"List");
		
		R<HashMap<String, Object>> result = 
				tokenManager.getDataFromToken(token, tokenConfig.getTokenKey(), tokenConfig.getTokenSignature(), tokenConfig.getTokenExpire(), keyNameMap);
		if(!ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
			json.setCode(result.getCode());
			json.setMessage(result.getMessage());
			return json;
		}
		HashMap<String, Object> dataMap = result.getData();
		WebUserEntity user = new WebUserEntity();
		user.setUserId(Integer.valueOf(dataMap.get(USER_ID_NAME).toString()));
		user.setCode(dataMap.get(CODE_NAME).toString());
		user.setRoles((ArrayList<Integer>)dataMap.get(ROLES_NAME));
		user.setUserType(dataMap.get(USER_TYPE_NAME).toString());
		
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
	public WebUser getUserFromRequest(HttpServletRequest request){
		String token = getTokenFromRequest(request);
		R<WebUser> json = getUserFromToken(token);
		if(json == null) {
			return null;
		}
		return json.getData();
	}
	
	/**
	 * 根据请求head中的“Login-Token”获取token值。
	 * @param request http请求信息
	 * @return String token值
	 *
	 * @author zhouhui
	 * @since 2021.11.28
	 */
	public String getTokenFromRequest(HttpServletRequest request){
		return request.getHeader(tokenConfig.getHeadTokenName());
	}
}
