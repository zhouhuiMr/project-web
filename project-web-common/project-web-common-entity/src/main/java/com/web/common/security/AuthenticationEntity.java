package com.web.common.security;

public class AuthenticationEntity {
	
	private AuthenticationEntity() {}

	/** 角色KEY的前缀描述 */
	public static final String ROLE_PREFIX = "ROLE::";
	
	/** 角色对应菜单KEY的前缀描述 */
	public static final String ROLE_MENU_PREFIX = "ROLE::MENU::";
	
	/** 用户唯一标识的前缀描述 */
	public static final String USER_PREFIX = "USER::";
	
	/** 用户的信息 */
	public static final String USER_KEY_INFO = "info";
	
	/** 用户的JWT */
	public static final String USER_KEY_TOKEN = "token";
	
	/** 允许访问 */
	public static final String ALLOW_ACCESS = "1";
	
	/** 禁止访问 */
	public static final String NO_ACCESS = "0";
	
	/**
	 * 获取redis中角色的key，如：ROLE::1。
	 * @param key
	 * @return String
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String getRoleKey(String key) {
		return ROLE_PREFIX + key;
	}
	
	/**
	 * 获取redis中角色对应菜单的key，如：ROLE::MENU::1。
	 * @param key 
	 * @return String
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String getRoleMenuKey(String key) {
		return ROLE_MENU_PREFIX + key;
	}
	
	/**
	 * 获取redis中用户的key，如：USER::1。
	 * @param key 
	 * @return String
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String getUserKey(String key) {
		return USER_PREFIX + key;
	}
}
