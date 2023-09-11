package com.web.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.user.entity.WebUserRole;
import com.web.common.user.entity.WebUserRoleEntity;

/**
 * <p>
 * 用户对应的角色关系 一个用户可以对应多个角色 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Mapper
public interface WebUserRoleMapper extends BaseMapper<WebUserRole> {

	/**
	 * 获取角色列表，并标记某个用户是否拥有此角色
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<WebUserRoleEntity> 角色列表
	 *
	 * @author zhouhui
	 * @since 2022.12.10
	 */
	IPage<WebUserRoleEntity> getUserRoleList(Page<WebUserRole> page, @Param("condition") WebUserRoleEntity condition);
}
