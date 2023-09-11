package com.web.workflow.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.workflow.api.WorkflowApi;
import com.web.common.workflow.entity.WorkflowDeployEntity;
import com.web.common.workflow.entity.WorkflowEntity;
import com.web.common.workflow.entity.WorkflowInstanceEntity;
import com.web.service.filter.resubmit.Resubmit;
import com.web.workflow.service.impl.WorkflowDeployServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "工作流部署")
@Controller
@RequestMapping(path = "/workflow/deploy")
public class WorkflowController implements WorkflowApi{
	
	@Autowired
	private WorkflowDeployServiceImpl workflowDeployServiceImpl;

	@Resubmit(interval = 10L)
	@Operation(summary = "工作流部署")
	@Override
	public R<Object> deploy(MultipartFile file, String name) {
		R<Object> json = new R<>();
		if(!StringUtils.hasText(name)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("未填写工作流名称");
			return json;
		}
		WorkflowDeployEntity entity = new WorkflowDeployEntity();
		try {
			entity.setWorkflowStream(file.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		entity.setWorkflowName(name);
		entity.setWorkflowKey(name);
		entity.setWorkflowFileName(file.getOriginalFilename());
		entity.setWorkflowStr(null);
		return workflowDeployServiceImpl.deploy(entity);
	}
	
	@Resubmit(interval = 10L)
	@Operation(summary = "工作流通过字符串部署")
	@Override
	public R<Object> deployByText(WorkflowDeployEntity workflow) {
		R<Object> json = new R<>();
		String name = workflow.getWorkflowName();
		String deployStr = workflow.getWorkflowStr();
		if(!StringUtils.hasText(name)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("未填写工作流名称");
			return json;
		}
		if(!StringUtils.hasText(deployStr)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("未填写工作流");
			return json;
		}
		WorkflowDeployEntity entity = new WorkflowDeployEntity();
		entity.setWorkflowName(name);
		entity.setWorkflowKey(name);
		entity.setWorkflowFileName(name + ".bpmn");
		entity.setWorkflowStr(deployStr);
		entity.setWorkflowStream(null);
		return workflowDeployServiceImpl.deploy(entity);
	}

	@Operation(summary = "获取工作流列表")
	@Override
	public R<IPage<WorkflowEntity>> getDeployList(WorkflowEntity condition) {
		return workflowDeployServiceImpl.getDeployList(condition);
	}

	@Resubmit(interval = 10L)
	@Operation(summary = "启动工作流")
	@Override
	public R<Object> workflowInstance(WorkflowInstanceEntity instance) {
		R<Object> json = new R<>();
		if(!StringUtils.hasText(instance.getDeployId())) {
			json.setResultEnum(ResultEnum.WORKFLOW_NO_DEPLOY_ID);
			return json;
		}
		return workflowDeployServiceImpl.workflowInstance(instance);
	}
}
