package com.web.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.user.entity.WebUser;
import com.web.common.user.entity.WebUserEntity;

@Mapper
public interface WebUserMapper extends BaseMapper<WebUser>{
	
	
	/**
	 * 获取用户信息列表
	 * @param page 分页信息
	 * @param user 用户信息查询条件
	 * @return 
	 *
	 * @author zhouhui
	 * @since 2022.02.07
	 */
	IPage<WebUserEntity> getUserList(Page<WebUser> page,@Param("user") WebUserEntity user);
}
