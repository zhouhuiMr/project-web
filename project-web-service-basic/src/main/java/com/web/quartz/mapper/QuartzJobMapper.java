package com.web.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.basic.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定时任务名称 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Mapper
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

}
