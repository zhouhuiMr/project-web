package com.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.user.entity.WebRequest;
import com.web.common.user.entity.WebRequestEntity;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Mapper
public interface WebRequestMapper extends BaseMapper<WebRequest> {

	/**
	 * 获取请求列表。
	 * @param request 查询条件
	 * @return 分页查询结果
	 *
	 * @author zhouhui
	 * @since 2022-05-17
	 */
	IPage<WebRequestEntity> getRequestList(IPage<?> page,@Param("request") WebRequestEntity request);
	
	/**
	 * 根据登录用户获取所在页面中授权的接口信息
	 * @param menuList 菜单ID列表
	 * @param roleList 角色ID列表
	 * @return List<WebRequestEntity> 查询到的接口列表
	 *
	 * @author zhouhui
	 * @since 2022-11-09
	 */
	List<WebRequestEntity> getAuthorisedRequest(@Param("menuList") List<Integer> menuList,@Param("userId") Integer userId);
}
