package com.web.common.user.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.WebRoleRequest;
import com.web.common.user.entity.WebRoleRequestEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "roleRequestApi", path = "/user/role/request")
public interface RoleRequestApi {

	/**
	 * 根据角色获取对应的所有接口，并区分是否有访问权限。<p>
	 * 可以根据角色和对应的菜单进行查询
	 * @param roleRequest 查询条件
	 * @return List<WebRoleRequestEntity> 角色对应的接口列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(path = "/authRequestList")
	@ResponseBody
	R<List<WebRoleRequestEntity>> getAuthRequest(@RequestBody WebRoleRequestEntity condition);
	
	/**
	 * 新增或者更新角色对应的请求接口
	 * @param roleRequest 角色对应的接口信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-09-18
	 */
	@PostMapping(path = "/saveOrUpdate/roleRequest")
	@ResponseBody
	R<Object> saveOrUpdateRoleRequest(@RequestBody @Valid WebRoleRequest roleRequest);
}
