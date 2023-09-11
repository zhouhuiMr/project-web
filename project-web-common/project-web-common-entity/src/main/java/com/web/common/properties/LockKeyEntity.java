package com.web.common.properties;

/**
 * 定义锁的key的名称
 * 
 * @author zhouhui
 * @since 1.0.0
 */
public final class LockKeyEntity {
	
	private LockKeyEntity() {}
	
	/** 初始化角色信息 */
	public static final String INIT_ROLE_LOCK = "initRoleLock";
	
	/** 创建文件目录 */
	public static final String CREATE_CATALOG_LOCK = "createCatalogLock";
}
