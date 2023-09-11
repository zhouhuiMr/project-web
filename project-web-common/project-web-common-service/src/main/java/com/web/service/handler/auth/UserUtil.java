package com.web.service.handler.auth;

import com.web.common.user.entity.WebUser;

/**
 * @author zhouhui
 * @since 1.0.0
 */
public class UserUtil {
	
	private UserUtil() {}

	private static final ThreadLocal<WebUser> USER_STORAGE = new ThreadLocal<>();
	
	/**
	 * 获取用户信息
	 * @return WebUser 用户信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static WebUser getUser() {
		return USER_STORAGE.get();
	}
	
	/**
	 * 将用户信息添加到ThreadLocal
	 * @param user
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static void setUser(WebUser user) {
		USER_STORAGE.set(user);
	}
	
	/**
	 * 清除ThreadLocal的值
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static void cleanThreadLocal() {
		USER_STORAGE.remove();
	}
}
