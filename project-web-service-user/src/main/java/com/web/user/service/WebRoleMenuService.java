package com.web.user.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.RoleAuthMenu;
import com.web.common.user.entity.WebRoleMenu;

public interface WebRoleMenuService extends IService<WebRoleMenu>{

	/**
	 * 根据角色获取所有的有效菜单，并标记此角色下有权限或者无权限的菜单。
	 * @param roleMenu 角色信息
	 * @return R<List<RoleAuthMenu>> 菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-05-14
	 */
	R<List<RoleAuthMenu>> getAuthMenuList(WebRoleMenu roleMenu);
	
	/**
	 * 保存或者更新角色对应的菜单信息及状态。
	 * <p>
	 * 1、判断角色是否存在；
	 * <p>
	 * 2、判断菜单是否存在；
	 * <p>
	 * 3、判断关系表中是否已经存在，存在进行更新；不存在进行添加。
	 * <p>
	 * 注：可能会存在并发情况，导致数据异常。
	 * @param roleMenu 角色对应的从菜单
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-15
	 */
	R<Object> saveOrUpdateRoleMenu(WebRoleMenu roleMenu);
}
