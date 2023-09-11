package com.web.user.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.WebRequest;
import com.web.common.user.entity.WebRequestEntity;

/**
 * <p>
 * 角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
public interface WebRequestService extends IService<WebRequest> {

	/**
	 * 获取请求接口列表。
	 * @param request 查询条件
	 * @return R<IPage<WebRequestEntity>> 获取请求列表
	 *
	 * @author zhouhui
	 * @since 2022-05-17
	 */
	R<IPage<WebRequestEntity>> getRequestList(WebRequestEntity request);
	
	/**
	 * 保存请求信息。
	 * @param request 请求信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-17
	 */
	R<Object> saveRequest(WebRequest request);
	
	/**
	 * 更新请求信息。
	 * @param request 请求的信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-17
	 */
	R<Object> updateRequest(WebRequest request);
	
	/**
	 * 删除请求信息。
	 * @param request 需要删除的请求信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-17
	 */
	R<Object> deleteRquest(WebRequest request);
	
	/**
	 * 根据登录用户获取所在页面中授权的接口信息。
	 * 注：只获当前页面授权的接口。
	 * @param menuList 菜单ID列表
	 * @return List<WebRequestEntity> 授权允许访问的接口
	 *
	 * @author zhouhui
	 * @since 2022-11-09
	 */
	R<List<WebRequestEntity>> getAuthorisedRequest(List<Integer> menuList);
}
