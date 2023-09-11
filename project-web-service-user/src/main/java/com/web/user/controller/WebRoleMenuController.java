package com.web.user.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.RoleMenuApi;
import com.web.common.user.entity.RoleAuthMenu;
import com.web.common.user.entity.WebRoleMenu;
import com.web.user.service.WebRoleMenuService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "角色与菜单对应关系")
@Controller
@RequestMapping(path = "/user/role/menu")
public class WebRoleMenuController implements RoleMenuApi{
	
	@Autowired
	private WebRoleMenuService webRoleMenuServiceImpl;

	@Operation(summary = "根据角色获取所有的有效菜单，并标记此角色下有权限或者无权限的菜单")
	@Override
	public R<List<RoleAuthMenu>> getAuthMenuList(WebRoleMenu roleMenu) {
		R<List<RoleAuthMenu>> json = new R<>();
		if(roleMenu.getRoleId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		json = webRoleMenuServiceImpl.getAuthMenuList(roleMenu);
		return json;
	}

	@Operation(summary = "保存或者更新角色对应的菜单信息及状态")
	@Override
	public R<Object> saveOrUpdateRoleMenu(WebRoleMenu roleMenu) {
		R<Object> json = new R<>();
		if(!Objects.equals(roleMenu.getDelFlag(), DataSourcesSymbol.DEL_FLAG_VALUE_0) && !Objects.equals(roleMenu.getDelFlag(), DataSourcesSymbol.DEL_FLAG_VALUE_1)) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		json = webRoleMenuServiceImpl.saveOrUpdateRoleMenu(roleMenu);
		return json;
	}
}
