package com.web.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.WebRole;
import com.web.common.user.entity.WebRoleEntity;

/**
 * <p>
 * 用户的角色 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
public interface WebRoleService extends IService<WebRole> {

	/**
	 * 获取角色信息列表
	 * @param condition 查询条件
	 * @return R<IPage<WebRoleEntity>> 角色信息列表
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	R<IPage<WebRoleEntity>> getRoleList(WebRoleEntity condition);
	
	/**
	 * 保存角色信息
	 * @param webRole 角色信息
	 * @return R<WebRoleEntity> 保存后的角色信息
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	R<WebRoleEntity> saveRole(WebRoleEntity webRole);
	
	/**
	 * 角色信息更新
	 * @param webRole 需要更新的角色信息
	 * @return R<WebRoleEntity> 更新后的角色信息
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	R<WebRoleEntity> updateRole(WebRoleEntity webRole);
	
	/**
	 * 标记删除角色信息
	 * @param webRole 需要删除的角色信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	R<Object> deleteRole(WebRoleEntity webRole);
}
