package com.web.common.workflow.entity;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "工作流实例参数", description = "工作流实例参数")
public class WorkflowInstanceEntity {

	/** 工作流部署的Id */
	@Schema(title = "工作流部署的Id")
	private String deployId;
	
	/** 工作流对应的业务Key */
	@Schema(title = "工作流对应的业务Key")
	private String businessKey;
	
	/** 任务参数 */
	@Schema(title = "任务参数")
	private Map<String, Object> taskParam;
}
