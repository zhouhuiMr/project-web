package com.web.common.basic.quartz.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 定时任务执行的日志信息
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-15
 */
@Getter
@Setter
@Schema(title = "QuartzJobExecLogEntity对象", description = "定时任务执行的日志信息")
public class QuartzJobExecLogEntity extends QuartzJobExecLog{

    private static final long serialVersionUID = 1L;
}
