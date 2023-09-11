package com.web.common.workflow.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.workflow.entity.WorkflowDeployEntity;
import com.web.common.workflow.entity.WorkflowEntity;
import com.web.common.workflow.entity.WorkflowInstanceEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_WORKFLOW_NAME, contextId = "workflowApi",path = "/workflow/deploy")
public interface WorkflowApi {

	/**
	 * 部署工作流
	 * @param file 工作流文件
	 * @param name 工作流名称
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023-04-13
	 */
	@PostMapping(path = "/")
	@ResponseBody
	R<Object> deploy(@RequestPart("file") MultipartFile file,@RequestParam("name") String name);
	
	/**
	 * 部署工作流
	 * @param file 工作流文件
	 * @param name 工作流名称
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023-04-13
	 */
	@PostMapping(path = "/deployByText")
	@ResponseBody
	R<Object> deployByText(@RequestBody WorkflowDeployEntity workflow);
	
	/**
	 * 根据查询条件查询已经部署的工作流列表
	 * @param condition 查询条件
	 * @return R<List<WorkflowEntity>> 工作流列表
	 * 
	 * @author zhouhui
	 * @since 2023-04-13
	 */
	@PostMapping(path = "/getDeployList")
	@ResponseBody
	R<IPage<WorkflowEntity>> getDeployList(@RequestBody WorkflowEntity condition);
	
	/**
	 * 创建启动工作流实例
	 * @param instance 工作流信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023-04-15
	 */
	@PostMapping(path = "/workflowInstance")
	@ResponseBody
	R<Object> workflowInstance(@RequestBody WorkflowInstanceEntity instance);
}
