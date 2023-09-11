package com.web.workflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.workflow.api.TaskApi;
import com.web.common.workflow.entity.TaskEntity;
import com.web.workflow.service.impl.TaskServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "工作流任务")
@Controller
@RequestMapping(path = "/workflow/task")
public class TaskController implements TaskApi{
	
	@Autowired
	private TaskServiceImpl taskServiceImpl;
	
	@Operation(summary = "根据创建人获取任务列表")
	@Override
	public R<IPage<TaskEntity>> getTaskListOfOwner(TaskEntity condition) {
		return taskServiceImpl.getTaskListOfOwner(condition);
	}

	@Operation(summary = "创建工作流任务")
	@Override
	public R<TaskEntity> createTask(TaskEntity task) {
		return taskServiceImpl.createTask(task);
	}
}
