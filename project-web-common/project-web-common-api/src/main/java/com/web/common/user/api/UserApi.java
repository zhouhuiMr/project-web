package com.web.common.user.api;

import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.WebUserEntity;
import com.web.common.user.entity.WebUserRoleEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "userApi", path = "/user")
public interface UserApi {
	
	/**
	 * 根据条件查询数据库存储的用户信息
	 * @param webUser 查询的条件
	 * @return R<IPage<WebUserEntity>> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2021.01.28
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<WebUserEntity>> getUserList(@RequestBody WebUserEntity webUser);
	
	/**
	 * 保存用户信息
	 * @param webUser 用户信息
	 * @return Object 
	 *
	 * @author zhouhui
	 * @since 2022.11.13
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<Object> saveUser(@RequestBody @Valid WebUserEntity webUser);
	
	/**
	 * 更新用户信息
	 * @param webUser 需要更新的用户信息
	 * @return Object 
	 *
	 * @author zhouhui
	 * @since 2022.11.13
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<Object> updateUser(@RequestBody WebUserEntity webUser);
	
	/**
	 * 删除用户信息
	 * @param webUser 需要删除的用户信息
	 * @return Object
	 *
	 * @author zhouhui
	 * @since 2022.11.13
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> deleteUser(@RequestBody WebUserEntity webUser);
	
	/**
	 * 重置用户密码
	 * @param webUser 用户密码
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.12.04
	 */
	@PostMapping(path = "/resetPassword")
	@ResponseBody
	R<Object> resetPassword(@RequestBody WebUserEntity webUser);
	
	/**
	 * 用户更新密码
	 * @param webUser 更新的密码信息
	 * @return
	 *
	 * @author zhouhui
	 * @since 2022.12.04
	 */
	@PostMapping(path = "/changePassword")
	@ResponseBody
	R<Object> changePassword(@RequestBody WebUserEntity webUser);
	
	/**
	 * 更新用户对应的角色
	 * @param userRole 用户对应的角色信息
	 * @return Object
	 *
	 * @author zhouhui
	 * @since 2022.11.14
	 */
	@PostMapping(path = "/updateUserRole")
	@ResponseBody
	R<Object> updateUserRole(@RequestBody @Valid WebUserRoleEntity userRole);
	
	/**
	 * 获取所有的角色，并匹配当前查询用户拥有的权限
	 * @param condition 查询条件
	 * @return R<IPage<WebUserRoleEntity>> 分页数据列表
	 *
	 * @author zhouhui
	 * @since 2022.12.10
	 */
	@PostMapping(path = "/getUserRoleList")
	@ResponseBody
	R<IPage<WebUserRoleEntity>> getUserRoleList(@RequestBody WebUserRoleEntity condition);
}
