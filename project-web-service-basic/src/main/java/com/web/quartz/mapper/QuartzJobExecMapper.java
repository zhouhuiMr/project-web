package com.web.quartz.mapper;

import com.web.common.basic.quartz.entity.QuartzJobExec;
import com.web.common.basic.quartz.entity.QuartzJobExecEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 定时任务执行相关信息 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Mapper
public interface QuartzJobExecMapper extends BaseMapper<QuartzJobExec> {

	/**
	 * 获取定时任务的列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<QuartzJobExecEntity> 分页数据列表
	 *
	 * @author zhouhui
	 * @since 2023.08.09 
	 */
	IPage<QuartzJobExecEntity> getJobList(IPage<QuartzJobExec> page, @Param("condition") QuartzJobExecEntity condition);
}
