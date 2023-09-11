package com.web.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.DeptApi;
import com.web.common.user.entity.WebDept;
import com.web.common.user.entity.WebDeptEntity;
import com.web.service.filter.resubmit.Resubmit;
import com.web.user.service.WebDeptService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 部门信息 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-05-08
 */
@Tag(name = "部门功能")
@Controller
@RequestMapping("/user/dept")
public class WebDeptController implements DeptApi{
	
	@Autowired
	private WebDeptService webDeptServiceImpl;

	@Operation(summary = "获取部门列表")
	@Override
	public R<List<WebDeptEntity>> getDeptList(WebDeptEntity condition) {
		return webDeptServiceImpl.getDeptList(condition);
	}

	@Resubmit
	@Operation(summary = "保存部门信息")
	@Override
	public R<Object> saveDept(WebDept dept) {
		return webDeptServiceImpl.saveDept(dept);
	}

	@Resubmit
	@Operation(summary = "更新部门信息")
	@Override
	public R<Object> updateDept(WebDept dept) {
		R<Object> json = new R<>();
		if(dept.getDeptId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("未选择修改的部门");
			return json;
		}
		return webDeptServiceImpl.updateDept(dept);
	}

	@Resubmit
	@Operation(summary = "删除部门信息")
	@Override
	public R<Object> deleteDept(WebDept dept) {
		R<Object> json = new R<>();
		if(dept.getDeptId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("未选择删除的部门");
			return json;
		}
		return webDeptServiceImpl.deleteDept(dept);
	}

}
