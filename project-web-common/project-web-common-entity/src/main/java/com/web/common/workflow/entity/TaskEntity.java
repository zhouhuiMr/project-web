package com.web.common.workflow.entity;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "任务信息", description = "任务信息")
public class TaskEntity {
	
	/** 任务的Id */
	@Schema(title = "任务的Id")
	private String taskId;
	
	/** 任务的父级ID */
	@Schema(title = "任务的父级ID")
	private String taskParentId;

	/** 任务名称 */
	@Schema(title = "任务名称")
	private String taskName;
	
	/** 任务描述 */
	@Schema(title = "任务描述")
	private String taskDescription;
	
	/** 任务优先级 */
	@Schema(title = "任务优先级")
	private Integer taskPriorit;
	
	/** 任务执行人 */
	@Schema(title = "任务执行人")
	private String taskAssignee;

	/**  */
	@Schema(title = "")
	private String fromKey;
	
	/** 任务截至日期 */
	@Schema(title = "任务截至日期")
	private LocalDate taskDueDate;
	
	/** 任务首选人 */
	@Schema(title = "任务首选人")
	private List<String> taskCandidateUsers;
	
	/** 任务状态 */
	@Schema(title = "任务状态")
	private String taskStatus;
	
	/** 当前页 */
	@Schema(title = "当前页")
	private int page = 1;
	
	/** 每页显示的数量 */
	@Schema(title = "每页显示的数量")
	private int pageSize = 10;
}
