package com.web.common.user.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.RoleAuthMenu;
import com.web.common.user.entity.WebRoleMenu;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "roleMenuApi", path = "/user/role/menu")
public interface RoleMenuApi {
	
	/**
	 * 根据角色获取所有的有效菜单，并标记此角色下有权限或者无权限的菜单。
	 * @param roleMenu 角色信息
	 * @return R<List<WebMenuEntity>> 菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-05-14
	 */
	@PostMapping(path = "/authMenuList")
	@ResponseBody
	R<List<RoleAuthMenu>> getAuthMenuList(@RequestBody WebRoleMenu roleMenu);
	
	
	/**
	 * 保存或者更新角色对应的菜单信息及状态。
	 * @param roleMenu 角色对应的菜单
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-14
	 */
	@PostMapping(path = "/saveOrUpdate/RoleMenu")
	@ResponseBody
	R<Object> saveOrUpdateRoleMenu(@RequestBody @Valid WebRoleMenu roleMenu);
}
