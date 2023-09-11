package com.web.common.workflow.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.workflow.entity.TaskEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_WORKFLOW_NAME, contextId = "taskApi",path = "/workflow/task")
public interface TaskApi {
	
	/**
	 * 根据任务创建人获取任务列表
	 * @param task 任务列表
	 * @return R<IPage<TaskEntity>> 任务列表分页
	 *
	 * @author zhouhui
	 * @since 2023.04.02
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<TaskEntity>> getTaskListOfOwner(@RequestBody TaskEntity condition);

	/**
	 * 创建工作流任务
	 * @param task 工作流任务信息
	 * @return R<TaskEntity> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.04.01
	 */
	@PostMapping(path = "/create")
	@ResponseBody
	R<TaskEntity> createTask(@RequestBody TaskEntity task);
}
