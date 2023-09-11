package com.web.common.user.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.WebDept;
import com.web.common.user.entity.WebDeptEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "deptApi",path = "/user/dept")
public interface DeptApi {

	/**
	 * 获取部门列表。
	 * 注：不支持模糊查询
	 * @param condition 查询条件
	 * @return R<List<WebDeptEntity>> 层级关系部门列表
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<List<WebDeptEntity>> getDeptList(@RequestBody WebDeptEntity condition);
	
	/**
	 * 保存部门信息
	 * @param dept 部门信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<Object> saveDept(@RequestBody @Valid WebDept dept);
	
	/**
	 * 更新部门信息
	 * @param dept 部门信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<Object> updateDept(@RequestBody @Valid WebDept dept);
	
	/**
	 * 删除部门信息
	 * @param dept 部门信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> deleteDept(@RequestBody WebDept dept);
}
