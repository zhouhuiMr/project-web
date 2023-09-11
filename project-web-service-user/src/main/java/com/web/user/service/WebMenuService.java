package com.web.user.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.UserMenu;
import com.web.common.user.entity.WebMenu;
import com.web.common.user.entity.WebMenuEntity;

/**
 * <p>
 * 对应的菜单 服务类
 * </p>
 *
 * @author zhouhui
 * @since 1.0.0
 */
public interface WebMenuService extends IService<WebMenu> {

	/**
	 * 获取菜单列表
	 * @param webMenu 查询条件
	 * @return 菜单列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<IPage<WebMenuEntity>> getMenuList(WebMenuEntity webMenu);
	
	/**
	 * 保存菜单信息。
	 * 1、判断字典项是否正确；
	 * 2、判断设置的菜单级别，如果一级菜单则设置parent_id = -1，否则判断parent_id是否存在;
	 * @param webMenu 需要保存的菜单信息
	 * @return 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<Object> saveMenu(WebMenu webMenu);
	
	/**
	 * 更新菜单信息
	 * @param webMenu 需要更新的菜单信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<Object> updateMenu(WebMenu webMenu);
	
	/**
	 * 标记删除菜单信息。
	 * <p>
	 * 判断此菜单是否存在子菜单，如果存在则禁止进行删除。
	 * @param webMenu 需要删除的菜单信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<Object> delMenu(WebMenu webMenu);
	
	
	/**
	 * 根据登录的用户的唯一标识获取菜单列表。
	 * @return 登录用户的菜单列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<List<UserMenu>> getUserMenu();
	
}
