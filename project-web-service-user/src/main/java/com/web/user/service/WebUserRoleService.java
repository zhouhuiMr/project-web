package com.web.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.WebUserRole;
import com.web.common.user.entity.WebUserRoleEntity;

/**
 * <p>
 * 用户对应的角色关系 一个用户可以对应多个角色 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
public interface WebUserRoleService extends IService<WebUserRole> {
	
	/**
	 * 获取所有的角色，并匹配当前查询用户拥有的权限
	 * @param userRole 用户角色的查询条件
	 * @return R<List<WebUserRoleEntity>> 获取用户列表
	 *
	 * @author zhouhui
	 * @since 2022.12.10
	 */
	R<IPage<WebUserRoleEntity>> getUserRoleList(WebUserRoleEntity condition);

	/**
	 * 单个更新用户对应的角色信息。<p>
	 * 注：<p>
	 * 1、根据authorityStatus判断是否授权某个角色给用户<p>
	 * 2、admin不允许修改角色，默认最高权限。
	 * @param userRole 用户与角色的对应关系
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.11.14
	 */
	R<Object> updateUserRole(WebUserRoleEntity userRole);
	
	/**
	 * 批量更新用户对应的角色信息。<p>
	 * 注：<p>
	 * 1、如果roleList没有值则认为取消用户的所有角色；<p>
	 * 2、admin不允许修改角色，默认最高权限。
	 * @param userRole 用户与角色的对应关系
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.11.14
	 */
	R<Object> batchUpdateUserRole(WebUserRoleEntity userRole);
}
