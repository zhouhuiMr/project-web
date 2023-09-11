package com.web.common.workflow.entity;

import java.io.InputStream;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "工作流部署信息", description = "工作流部署信息")
public class WorkflowDeployEntity {
	
	/** 工作流的Id */
	@Schema(title = "工作流的Id")
	private String id;
	
	/** 工作流名称 */
	@Schema(title = "工作流名称")
	private String workflowName;
	
	/** 工作流的Key */
	@Schema(title = "工作流的Key")
	private String workflowKey;
	
	/** 工作流的文件名称 */
	@Schema(title = "工作流的文件名称")
	private String workflowFileName;
	
	/** 工作流输入流 */
	@Schema(title = "工作流输入流")
	private InputStream workflowStream;
	
	/** 工作流字符串 */
	@Schema(title = "工作流字符串")
	private String workflowStr;
	
	
}
