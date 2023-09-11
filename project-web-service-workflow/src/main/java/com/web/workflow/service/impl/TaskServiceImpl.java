package com.web.workflow.service.impl;

import java.util.List;

import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.CreateTaskPayloadBuilder;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;
import com.web.common.user.entity.WebUser;
import com.web.common.workflow.entity.TaskEntity;
import com.web.config.init.SecurityUtil;
import com.web.service.handler.auth.UserUtil;

@Service
public class TaskServiceImpl {
	
	@Autowired
	private TaskRuntime taskRuntime;
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private SecurityUtil securityUtil;
	
	public R<IPage<TaskEntity>> getTaskListOfOwner(TaskEntity condition) {
		WebUser loginUser = UserUtil.getUser();
		securityUtil.logInAs(loginUser.getCode());
		
		
		
		Pageable page = Pageable.of((condition.getPage() - 1) * condition.getPageSize(), condition.getPageSize());
		
		Page<Task> taskList = taskRuntime.tasks(page, null);
		System.out.println(taskList.getContent().size());
		return null;
	}

	public R<TaskEntity> createTask(TaskEntity entity) {
		R<TaskEntity> json = new R<>();
		if(!StringUtils.hasText(entity.getTaskAssignee())) {
			json.setResultEnum(ResultEnum.WORKFLOW_TASK_NO_ASSIGNEE);
			return json;
		}
		WebUser loginUser = UserUtil.getUser();
		securityUtil.logInAs(loginUser.getCode());
		
		CreateTaskPayloadBuilder task = TaskPayloadBuilder.create();
		task.withName(entity.getTaskName());
		task.withDescription(entity.getTaskDescription());
		if(entity.getTaskPriorit() != null) {
			task.withPriority(entity.getTaskPriorit());
		}
		task.withAssignee(entity.getTaskAssignee());
		List<String> candidateUsers = entity.getTaskCandidateUsers();
		if(candidateUsers != null && !candidateUsers.isEmpty()) {
			task.withCandidateUsers(candidateUsers);
		}
		task.withFormKey(entity.getFromKey());
		task.withParentTaskId(entity.getTaskParentId());
		if(entity.getTaskDueDate() != null) {
			task.withDueDate(DateTimeUtil.localDateToDate(entity.getTaskDueDate()));
		}
		Task result = taskRuntime.create(task.build());
		entity.setTaskId(result.getId());
		entity.setTaskStatus(result.getStatus().name());

		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(entity);
		return json;
	}
	
	
}
