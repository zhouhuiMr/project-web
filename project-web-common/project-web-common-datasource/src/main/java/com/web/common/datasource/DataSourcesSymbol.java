package com.web.common.datasource;

public class DataSourcesSymbol {
	
	private DataSourcesSymbol() {}

	/** del_flag 0未删除 */
	public static final Integer DEL_FLAG_VALUE_0 = 0;
	
	/** del_flag 1已删除 */
	public static final Integer DEL_FLAG_VALUE_1 = 1;
	
	/** 与用户状态 0注册 */
	public static final Integer USER_STATUS_0 = 0;
	
	/** 与用户状态 1可登录系统 */
	public static final Integer USER_STATUS_1 = 1;
	
	/** 与用户状态 2无效 */
	public static final Integer USER_STATUS_2 = 2;
	
	/** 用户对应角色授权，0未授权 */
	public static final Integer USER_ROLE_STATUS_0 = 0;
	
	/** 用户对应角色授权，1已授权 */
	public static final Integer USER_ROLE_STATUS_1 = 1;
	
	/** 设备的状态，初始化 */
	public static final Integer DEVICE_STATUS_0 = 0;
	
	/** 设备的状态，离线 */
	public static final Integer DEVICE_STATUS_1 = 1;
	
	/** 设备的状态，在线 */
	public static final Integer DEVICE_STATUS_2 = 2;
}
