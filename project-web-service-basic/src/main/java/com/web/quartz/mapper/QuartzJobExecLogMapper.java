package com.web.quartz.mapper;

import com.web.common.basic.quartz.entity.QuartzJobExecLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定时任务执行的日志信息 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-15
 */
@Mapper
public interface QuartzJobExecLogMapper extends BaseMapper<QuartzJobExecLog> {

}
