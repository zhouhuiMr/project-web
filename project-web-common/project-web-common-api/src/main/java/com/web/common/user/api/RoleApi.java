package com.web.common.user.api;

import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.WebRoleEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "roleApi", path = "/user/role")
public interface RoleApi {

	/**
	 * 获取角色列表
	 * @param webRole 角色信息查询条件
	 * @return R<IPage<WebRoleEntity>> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<WebRoleEntity>> getRoleList(@RequestBody WebRoleEntity condition);
	
	/**
	 * 保存角色信息
	 * @param webRole 角色信息
	 * @return R<WebRoleEntity> 保存的信息
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<WebRoleEntity> saveRole(@RequestBody @Valid WebRoleEntity webRole);
	
	/**
	 * 更新角色信息
	 * @param webRole 更新的角色信息
	 * @return R<WebRoleEntity> 更新后的角色信息
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<WebRoleEntity> updateRole(@RequestBody @Valid WebRoleEntity webRole);
	
	/**
	 * 标记删除角色信息
	 * @param webRole 需要删除的角色信息
	 * @return 
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> deleteRole(@RequestBody WebRoleEntity webRole);
}
