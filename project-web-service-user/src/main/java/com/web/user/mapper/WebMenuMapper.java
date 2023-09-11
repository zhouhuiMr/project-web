package com.web.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.user.entity.UserMenu;
import com.web.common.user.entity.WebMenu;
import com.web.common.user.entity.WebMenuEntity;
import com.web.common.user.entity.WebRoleMenu;

/**
 * <p>
 * 对应的菜单 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-17
 */
@Mapper
public interface WebMenuMapper extends BaseMapper<WebMenu> {

	/**
	 * 获取菜单列表
	 * @param page 分页信息
	 * @param webMenu 查询条件
	 * @return IPage<WebMenu> 数据列表
	 *
	 * @author zhouhui
	 * @since 2022-04-20
	 */
	IPage<WebMenuEntity> getMenuList(IPage<?> page, @Param("webMenu") WebMenuEntity webMenu);
	
	/**
	 * 获取登录用户的显示的菜单。
	 * 注：一个用户可能会对应多个角色，因此需要将查询结果去重。
	 * @param userId 登录用户的唯一标识
	 * @return List<UserMenu> 菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-05-09
	 */
	List<UserMenu> getUserMenu(@Param("userId") Integer userId);
	
	/**
	 * 根据角色获取菜单权限。
	 * @param roleId 角色唯一标识
	 * @return List<WebMenuEntity> 菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-05-15
	 */
	List<WebMenuEntity> getAuthMenuList(@Param("roleMenu") WebRoleMenu roleMenu);
}
