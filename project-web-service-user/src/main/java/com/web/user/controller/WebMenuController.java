package com.web.user.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.MenuApi;
import com.web.common.user.entity.UserMenu;
import com.web.common.user.entity.WebMenuEntity;
import com.web.user.service.impl.WebMenuServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 对应的菜单 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-17
 */
@Tag(name = "菜单功能")
@Controller
@RequestMapping(path = "/user/menu")
public class WebMenuController implements MenuApi{
	
	@Autowired
	private WebMenuServiceImpl webMenuServiceImpl;

	@Operation(summary = "获取菜单列表")
	@Override
	public R<IPage<WebMenuEntity>> getMenuList(WebMenuEntity webMenuEntity) {
		return webMenuServiceImpl.getMenuList(webMenuEntity);
	}

	@Operation(summary = "菜单新增")
	@Override
	public R<Object> saveMenu(WebMenuEntity webMenu) {
		return webMenuServiceImpl.saveMenu(webMenu);
	}

	@Operation(summary = "菜单更新")
	@Override
	public R<Object> updateMenu(WebMenuEntity webMenu) {
		R<Object> json = new R<>();
		if(webMenu.getMenuId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请选择需要修改的菜单");
			return json;
		}
		return webMenuServiceImpl.updateMenu(webMenu);
	}

	@Operation(summary = "菜单标记删除")
	@Override
	public R<Object> delMenu(WebMenuEntity webMenu) {
		R<Object> json = new R<>();
		if(webMenu.getMenuId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请选择需要删除的菜单");
			return json;
		}
		return webMenuServiceImpl.delMenu(webMenu);
	}

	@Operation(summary = "获取当前登录用户的菜单")
	@Override
	public R<List<UserMenu>> getUserMenu() {
		return webMenuServiceImpl.getUserMenu();
	}
}
