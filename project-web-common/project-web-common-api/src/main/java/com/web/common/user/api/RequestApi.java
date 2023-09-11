package com.web.common.user.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.WebRequest;
import com.web.common.user.entity.WebRequestEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "requestApi", path = "/user/request")
public interface RequestApi {
	
	/**
	 * 获取请求列表
	 * @param request 查询条件
	 * @return R<IPage<WebRequestEntity>> 请求列表
	 *
	 * @author zhouhui
	 * @since 2022-05-16
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<WebRequestEntity>> getRequestList(@RequestBody WebRequestEntity request);
	
	/**
	 * 保存请求信息
	 * @param request 保存的信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-16
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<Object> saveRequest(@RequestBody @Valid WebRequest request);
	
	/**
	 * 更新请求信息
	 * @param request 更新的信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-16
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<Object> updateRequest(@RequestBody @Valid WebRequest request);
	
	/**
	 * 删除请求信息
	 * @param request 删除的信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-16
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> deleteRequest(@RequestBody WebRequest request);
	
	
	/**
	 * 根据登录用户获取所在页面中授权的接口信息
	 * @param request 查询条件
	 * @return List<WebRequestEntity> 接口列表
	 *
	 * @author zhouhui
	 * @since 2022-11-09
	 */
	@PostMapping(path = "/getAuthorisedRequest")
	@ResponseBody
	R<List<WebRequestEntity>> getAuthorisedRequest(@RequestBody List<Integer> menuList);
}
