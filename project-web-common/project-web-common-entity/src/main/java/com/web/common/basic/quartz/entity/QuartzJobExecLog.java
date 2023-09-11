package com.web.common.basic.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("quartz_job_exec_log")
@Schema(title = "QuartzJobExecLog对象", description = "定时任务执行的日志信息")
public class QuartzJobExecLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    /** quartz_job_exec的id */
    @Schema(title = "quartz_job_exec的id")
    private Integer execId;

    /** 0固定频率；1cron表达式，字典项quartz_job_period */
    @Schema(title = "0固定频率；1cron表达式，字典项quartz_job_period")
    private Integer execPeriod;

    /** 类型对应的值 */
    @Schema(title = "类型对应的值")
    private String execPeriodValue;

    /** 触发方式，0自动；1手动 */
    @Schema(title = "触发方式，0自动；1手动")
    private Integer triggerMode;

    /** 开始时间 */
    @Schema(title = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(title = "结束时间")
    private LocalDateTime endTime;

    /** 执行结果 */
    @Schema(title = "执行结果")
    private String execResult;

    /** 日志写入时间 */
    @Schema(title = "日志写入时间")
    private LocalDateTime createTime;


}
