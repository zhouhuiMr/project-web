package com.web.common.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.web.common.properties.TokenConfigEntity;
import com.web.common.token.JWTTokenManager;

/**
 * 默认的系统用户及角色
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class SysDefaultAdmin {
	
	/** 默认用户的ID */
	public static final int SYSTEM_ADMIN_ID = 1;
	
	/** 默认用户的CODE */
	public static final String SYSTEM_ADMIN_CODE = "admin";

	/** 默认的管理员角色ID */
	public static final int SYSTEM_DEFAULT_ROLE = 1;
	
	/** userId */
	public static final String USER_ID_NAME = "userId";
	
	/** code */
	public static final String CODE_NAME = "code";
	
	/** roles */
	public static final String ROLES_NAME = "roles";
	
	/** userType */
	public static final String USER_TYPE_NAME = "userType";
	
	private JWTTokenManager tokenManager = new JWTTokenManager();
	
	@Autowired
	private TokenConfigEntity tokenConfig;
	
	/**
	 * 生成默认的token信息，对应的用户是系统管理员
	 * @return String 生成的token字符串
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public String createDefaultToken() {
		List<Integer> roleList = new ArrayList<>();
		roleList.add(SysDefaultAdmin.SYSTEM_DEFAULT_ROLE);
		
		HashMap<String, Object> option = new HashMap<>();
		option.put(USER_ID_NAME, SysDefaultAdmin.SYSTEM_ADMIN_ID);
		option.put(CODE_NAME, SysDefaultAdmin.SYSTEM_ADMIN_CODE);
		option.put(ROLES_NAME, roleList);
		option.put(USER_TYPE_NAME, "1");
		return tokenManager.createToken(option, tokenConfig.getTokenKey(), tokenConfig.getTokenSignature(), tokenConfig.getTokenExpire());
	}
}
