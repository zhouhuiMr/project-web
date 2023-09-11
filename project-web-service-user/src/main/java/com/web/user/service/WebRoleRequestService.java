package com.web.user.service;

import com.web.common.result.R;
import com.web.common.user.entity.WebRoleRequest;
import com.web.common.user.entity.WebRoleRequestEntity;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-09-17
 */
public interface WebRoleRequestService extends IService<WebRoleRequest> {

	/**
	 * 根据角色获取对应的所有接口，并区分是否有访问权限。<p>
	 * 可以根据角色和对应的菜单进行查询。
	 * @param condition 查询条件
	 * @return List<WebRoleRequestEntity> 角色对应的接口列表
	 *
	 * @author zhouhui
	 * @since 2022-09-18
	 */
	R<List<WebRoleRequestEntity>> getAuthRequest(WebRoleRequestEntity condition);
	
	/**
	 * 保存或者更新角色对应接口。
	 * 1、判断角色是否存在；
	 * 2、判断接口是否存在；
	 * 3、判断角色对应的接口是否存在，存在则进行更新，否则进行新增；
	 * 4、更新角色对应接口的缓存。
	 * @param roleRequest 角色对应接口
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-09-18
	 */
	R<Object> saveOrUpdateRoleRequest(WebRoleRequest roleRequest);
	
	/**
	 * 根据角色获取允许访问的接口。
	 * 存储的数据结构
	 * {"roleKey": {"/a": "1","/b": "1"}}
	 * @param roleList 角色列表
	 * @return List<WebRoleRequest> 接口列表
	 *
	 * @author zhouhui
	 * @since 2022-09-19
	 */
	R<HashMap<String, HashMap<String, String>>> getAllowRequest(List<Integer> roleList);
}
