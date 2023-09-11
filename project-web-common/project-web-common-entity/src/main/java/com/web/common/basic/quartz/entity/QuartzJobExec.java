package com.web.common.basic.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 定时任务执行相关信息
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Getter
@Setter
@TableName("quartz_job_exec")
@Schema(title = "QuartzJobExec对象", description = "定时任务执行相关信息")
public class QuartzJobExec implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "exec_id", type = IdType.AUTO)
    private Integer execId;

    /** quartz_job表的id */
    @Schema(title = "quartz_job表的id")
    private Integer jobId;

    /** 0固定频率；1cron表达式，字典项quartz_job_period */
    @NotNull(message = "未设置执行频率")
    @Schema(title = "0固定频率；1cron表达式，字典项quartz_job_period")
    private Integer execPeriod;

    /** 类型对应的值 */
    @NotBlank(message = "未设置频率对应的值")
    @Schema(title = "类型对应的值")
    private String execPeriodValue;

    /** 0http请求；1bean方式，字典项quartz_job_mode */
    @NotNull(message = "未设置执行方式")
    @Schema(title = "0http请求；1bean方式，字典项quartz_job_mode")
    private Integer execMode;
    
    /** http请求地址 */
    @Schema(title = "http请求地址")
    private String httpUrl;

    /** http请求方式 */
    @Schema(title = "http请求方式")
    private String httpMethod;

    /** http请求的header的content-type */
    @Schema(title = "http请求的header的content-type")
    private String httpContentType;

    /** bean方式的bean名称 */
    @Schema(title = "bean方式的bean名称")
    private String beanName;

    /** bean方式的方法名称 */
    @Schema(title = "bean方式的方法名称")
    private String beanMethod;

    /** 执行方法的参数 */
    @Schema(title = "执行方法的参数")
    private String execParam;

    /** 启用状态，0、启用；1、未启用； */
    @Schema(title = "启用状态，0、启用；1、未启用；")
    private Integer jobStatus;

    /** 是否删除，0否；1是 */
    @Schema(title = "是否删除，0否；1是")
    private Integer delFlag;

    /** 创建人的唯一标识 */
    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人的唯一标识 */
    @Schema(title = "修改人的唯一标识")
    private Integer changeUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime changeTime;


}
