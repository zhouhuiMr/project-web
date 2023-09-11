package com.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.user.entity.WebDept;
import com.web.common.user.entity.WebDeptEntity;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 部门信息 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023.05.08
 */
@Mapper
public interface WebDeptMapper extends BaseMapper<WebDept> {

	/**
	 * 获取部门列表
	 * @param condition 查询条件
	 * @return List<WebDeptEntity 部门列表
	 * 
	 * @author zhouhui
	 * @since 2023.05.14
	 */
	List<WebDeptEntity> getDeptList(@Param("condition") WebDeptEntity condition);
}
