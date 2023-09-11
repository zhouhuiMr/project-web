package com.web.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.UserApi;
import com.web.common.user.entity.WebUserEntity;
import com.web.common.user.entity.WebUserRoleEntity;
import com.web.user.service.WebUserRoleService;
import com.web.user.service.impl.WebUserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 
 * @since 2021.01.11
 * @author zhouhui
 */
@Tag(name = "用户功能")
@Controller
@RequestMapping(path = "/user")
public class WebUserController implements UserApi{
	
	@Autowired
	private WebUserServiceImpl webUserServiceImpl;
	
	@Autowired
	private WebUserRoleService webUserRoleServiceImpl;

	@Operation(summary = "获取用户列表")
	@Override
	public R<IPage<WebUserEntity>> getUserList(WebUserEntity webUser) {
		return webUserServiceImpl.getUserList(webUser);
	}

	@Operation(summary = "保存用户信息")
	@Override
	public R<Object> saveUser(WebUserEntity webUser) {
		return webUserServiceImpl.saveUser(webUser);
	}

	@Operation(summary = "更新用户信息")
	@Override
	public R<Object> updateUser(WebUserEntity webUser) {
		R<Object> json = new R<>();
		if(webUser.getUserId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请选择要修改的用户");
			return json;
		}
		return webUserServiceImpl.updateUser(webUser);
	}

	@Operation(summary = "删除用户信息")
	@Override
	public R<Object> deleteUser(WebUserEntity webUser) {
		R<Object> json = new R<>();
		if(webUser.getUserId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请选择要删除的用户");
			return json;
		}
		return webUserServiceImpl.deleteUser(webUser);
	}

	@Operation(summary = "更新用户的角色信息")
	@Override
	public R<Object> updateUserRole(WebUserRoleEntity userRole) {
		return webUserRoleServiceImpl.updateUserRole(userRole);
	}

	@Operation(summary = "重置用户密码")
	@Override
	public R<Object> resetPassword(WebUserEntity webUser) {
		return webUserServiceImpl.resetPassword(webUser);
	}

	@Operation(summary = "用户更改密码")
	@Override
	public R<Object> changePassword(WebUserEntity webUser) {
		return webUserServiceImpl.changePassword(webUser);
	}

	@Operation(summary = "获取所有角色，并匹配查询用户")
	@Override
	public R<IPage<WebUserRoleEntity>> getUserRoleList(WebUserRoleEntity condition) {
		R<IPage<WebUserRoleEntity>> json = new R<>();
		if(condition.getUserId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请先选择用户信息");
			return json;
		}
		return webUserRoleServiceImpl.getUserRoleList(condition);
	}
}
