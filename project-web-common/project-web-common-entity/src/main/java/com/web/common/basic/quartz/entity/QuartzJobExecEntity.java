package com.web.common.basic.quartz.entity;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

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
@Schema(title = "QuartzJobExecEntity对象", description = "定时任务执行相关信息")
public class QuartzJobExecEntity extends QuartzJobExec {

	private static final long serialVersionUID = 1L;

	/** 定时任务名称 */
	@Length(max = 100, message = "任务名称不能超过100个字符")
	@NotBlank(message = "未填写任务名称")
	@Schema(title = "定时任务名称")
	private String jobName;

	/** 任务备注说明 */
	@Length(max = 500, message = "任务备注不能超过500个字符")
	@Schema(title = "任务备注说明")
	private String jobRemark;

	/** 执行频率，0固定频率；1corn表达式 */
	@Schema(title = "执行频率，0固定频率；1corn表达式")
	private String execPeriodName;

	/** 执行方式，0http请求；1bean方式 */
	@Schema(title = "执行方式，0http请求；1bean方式")
	private String execModeName;
	
	/** 执行id的列表 */
	@Schema(title = "执行id的列表")
	private List<Integer> execIdList;

	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;

	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
