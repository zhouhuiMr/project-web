package com.web.common.workflow.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "工作流信息", description = "工作流信息")
public class WorkflowEntity {
	
	/** 工作流部署的ID */
	@Schema(title = "工作流部署的ID")
	private String deployId;

	/** 工作流名称 */
	@Schema(title = "工作流名称")
	private String name;
	
	/** 工作流的KEY */
	@Schema(title = "工作流的KEY")
	private String key;
	
	/** 工作流部署时间 */
	@Schema(title = "工作流部署时间")
	private String deployTime;
	
	/** 工作流版本 */
	@Schema(title = "工作流版本")
	private Integer version;
	
	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
