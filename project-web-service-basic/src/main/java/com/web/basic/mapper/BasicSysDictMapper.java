package com.web.basic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.basic.entity.BasicSysDictEntity;

/**
 * <p>
 * 系统的字典项配置  Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-30
 */
@Mapper
public interface BasicSysDictMapper extends BaseMapper<BasicSysDict> {

	/**
	 * 获取字典分页列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<BasicSysDict> 字典项分页列表
	 * 
	 * @author zhouhui
	 * @since 2023-01-02
	 */
	IPage<BasicSysDict> getDictList(IPage<BasicSysDict> page, @Param("condition") BasicSysDictEntity condition);
}
