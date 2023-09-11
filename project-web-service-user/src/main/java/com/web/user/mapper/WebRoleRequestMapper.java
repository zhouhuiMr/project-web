package com.web.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.user.entity.WebRoleRequest;
import com.web.common.user.entity.WebRoleRequestEntity;

/**
 * <p>
 * 角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Mapper
public interface WebRoleRequestMapper extends BaseMapper<WebRoleRequest> {
	
	/**
	 * 根据角色获取对应的所有接口，并区分是否有访问权限。<p>
	 * 可以根据角色和对应的菜单进行查询。
	 * @param condition 查询条件
	 * @return List<WebRoleRequestEntity> 角色对应的接口列表
	 *
	 * @author zhouhui
	 * @since 2022-09-18
	 */
	List<WebRoleRequestEntity> getAuthRequest(@Param("condition") WebRoleRequestEntity condition);
	
	/**
	 * 根据角色获取允许访问的接口列表
	 * @param roleList 角色列表
	 * @return List<WebRoleRequest> 允许访问的接口
	 *
	 * @author zhouhui
	 * @since 2022-09-19
	 */
	List<WebRoleRequestEntity> getAllowRequest(@Param("roleList") List<Integer> roleList);
}
