package com.web.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.RoleApi;
import com.web.common.user.entity.WebRoleEntity;
import com.web.user.service.WebRoleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "角色信息")
@Controller
@RequestMapping(path = "/user/role")
public class WebRoleController implements RoleApi{
	
	@Autowired
	private WebRoleService webRoleService;

	@Operation(summary = "获取角色列表")
	@Override
	public R<IPage<WebRoleEntity>> getRoleList(WebRoleEntity condition) {
		return webRoleService.getRoleList(condition);
	}

	@Operation(summary = "保存角色信息")
	@Override
	public R<WebRoleEntity> saveRole(@Valid WebRoleEntity webRole) {
		return webRoleService.saveRole(webRole);
	}

	@Operation(summary = "更新角色信息")
	@Override
	public R<WebRoleEntity> updateRole(@Valid WebRoleEntity webRole) {
		if(webRole.getRoleId() == null) {
			R<WebRoleEntity> json = new R<>();
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择更新的角色信息");
			return json;
		}
		return webRoleService.updateRole(webRole);
	}

	@Operation(summary = "删除角色信息")
	@Override
	public R<Object> deleteRole(WebRoleEntity webRole) {
		if(webRole.getRoleId() == null) {
			R<Object> json = new R<>();
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择删除的角色信息");
			return json;
		}
		return webRoleService.deleteRole(webRole);
	}
}
