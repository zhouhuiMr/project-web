package com.web.user.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.user.entity.WebRole;
import com.web.common.user.entity.WebRoleEntity;
import com.web.common.user.entity.WebUserEntity;

/**
 * <p>
 * 用户的角色 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Mapper
public interface WebRoleMapper extends BaseMapper<WebRole> {
	
	/**
	 * 根据查询条件，获取角色列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<WebRoleEntity> 获取角色列表
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	IPage<WebRoleEntity> getRoleList(Page<WebRole> page, @Param("condition") WebRoleEntity condition);

	/**
	 * 根据用户信息获取对应的角色列表
	 * @param user 用户信息
	 * @return ArrayList<WebRoleEntity> 角色列表
	 *
	 * @author zhouhui
	 * @since 2022.02.08
	 */
	ArrayList<WebRoleEntity> getRolesByUser(@Param("user") WebUserEntity user);
}
