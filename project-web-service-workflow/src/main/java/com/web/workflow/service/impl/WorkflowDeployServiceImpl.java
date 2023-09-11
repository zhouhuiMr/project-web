package com.web.workflow.service.impl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;
import com.web.common.workflow.entity.WorkflowDeployEntity;
import com.web.common.workflow.entity.WorkflowEntity;
import com.web.common.workflow.entity.WorkflowInstanceEntity;

@Service
public class WorkflowDeployServiceImpl {
	
	private static final String WORKFLOW_CATEGORY = "CUSTOM_WORKFLOW";
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;

	/**
	 * 工作流部署。
	 * 相同名称的工作流不能进行部署，
	 * 需要删除之前的工作流，然后重新部署。
	 * @param entity 工作流信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023-04-13
	 */
	public R<Object> deploy(WorkflowDeployEntity entity) {
		R<Object> json = new R<>();
		DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
		deploymentQuery.deploymentKey(entity.getWorkflowKey());
		deploymentQuery.deploymentName(entity.getWorkflowName());
		long deploymentCount = deploymentQuery.count();
		if(deploymentCount > 0) {
			json.setResultEnum(ResultEnum.DATA_QUERY_SAME);
			return json;
		}
		
		DeploymentBuilder deployment = repositoryService.createDeployment();
		deployment.name(entity.getWorkflowName());
		deployment.key(entity.getWorkflowKey());
		deployment.category(WORKFLOW_CATEGORY);
		if(entity.getWorkflowStream() != null) {
			InputStream in = entity.getWorkflowStream();
			deployment.addInputStream(entity.getWorkflowFileName(), in);
		}else {
			if(StringUtils.hasText(entity.getWorkflowStr())) {
				String str = new String(Base64Utils.decodeFromString(entity.getWorkflowStr()), StandardCharsets.UTF_8);
				deployment.addString(entity.getWorkflowFileName(), str);
			}
		}
		deployment.deploy();

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	/**
	 * 根据条件查询工作流
	 * @param condition 查询条件
	 * @return R<IPage<WorkflowEntity>> 工作流列表
	 * 
	 * @author zhouhui
	 * @since 2023-04-13
	 */
	public R<IPage<WorkflowEntity>> getDeployList(WorkflowEntity condition) {
		R<IPage<WorkflowEntity>> json = new R<>();
		String key = condition.getKey();
		String name = condition.getName();
		
		DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
		if(StringUtils.hasText(key)) {
			deploymentQuery.deploymentKeyLike(key);
		}
		if(StringUtils.hasText(name)) {
			deploymentQuery.deploymentNameLike(name);
		}
		deploymentQuery.deploymentCategory(WORKFLOW_CATEGORY);
		deploymentQuery.orderByDeploymenTime().asc();
		
		List<Deployment> dataList = null;
		if(condition.getSize() > 0) {
			dataList = deploymentQuery.listPage(condition.getPage(), condition.getSize());
		}else {
			dataList = deploymentQuery.list();
		}
		
		if(dataList == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("查询工作流列表异常");
			return json;
		}
		List<WorkflowEntity> workflowList = new ArrayList<>(dataList.size());
		SimpleDateFormat format = new SimpleDateFormat(DateTimeUtil.YEAR_MONTH_DATE_TIME);
		for(Deployment item : dataList) {
			WorkflowEntity entity = new WorkflowEntity();
			entity.setDeployId(item.getId());
			entity.setName(item.getName());
			entity.setKey(item.getKey());
			entity.setVersion(item.getVersion());
			entity.setDeployTime(format.format(item.getDeploymentTime()));
			workflowList.add(entity);
		}
		
		IPage<WorkflowEntity> result = new Page<>();
		result.setRecords(workflowList);
		result.setTotal(deploymentQuery.count());
		result.setPages(condition.getPage());
		result.setSize(condition.getSize());
		result.setCurrent(condition.getPage());
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(result);
		return json;
	}
	
	/**
	 * 启动工作流
	 * @param instance 工作流启动信息
	 * @return R<Object>处理结果
	 *
	 * @author zhouhui
	 * @since 2023.04.15
	 */
	public R<Object> workflowInstance(WorkflowInstanceEntity instance) {
		R<Object> json = new R<>();
		ProcessDefinitionQuery processQuery = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition process = processQuery.deploymentId(instance.getDeployId()).singleResult();
		if(process == null) {
			json.setResultEnum(ResultEnum.WORKFLOW_NO_INSTANCE);
			return json;
		}
		runtimeService.startProcessInstanceById(process.getId(), instance.getBusinessKey(), instance.getTaskParam());
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
}
