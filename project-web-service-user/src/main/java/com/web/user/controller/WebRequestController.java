package com.web.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.RequestApi;
import com.web.common.user.entity.WebRequest;
import com.web.common.user.entity.WebRequestEntity;
import com.web.service.handler.log.IgnoreOperateLogs;
import com.web.user.service.WebRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Tag(name = "接口维护功能")
@Controller
@RequestMapping(path = "/user/request")
public class WebRequestController implements RequestApi{
	
	@Autowired
	private WebRequestService webRequestServiceImpl;

	@Operation(summary = "获取接口列表")
	@Override
	public R<IPage<WebRequestEntity>> getRequestList(WebRequestEntity request) {
		return webRequestServiceImpl.getRequestList(request);
	}

	@Operation(summary = "保存接口列表")
	@Override
	public R<Object> saveRequest(WebRequest request) {
		return webRequestServiceImpl.saveRequest(request);
	}

	@Operation(summary = "更新接口信息")
	@Override
	public R<Object> updateRequest(WebRequest request) {
		R<Object> json = new R<>();
		if(request.getRequestId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("未选择要修改的接口信息");
			return json;
		}
		json = webRequestServiceImpl.updateRequest(request);
		return json;
	}

	@Operation(summary = "删除接口信息")
	@Override
	public R<Object> deleteRequest(WebRequest request) {
		R<Object> json = new R<>();
		if(request.getRequestId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("未选择要删除的接口信息");
			return json;
		}
		json = webRequestServiceImpl.deleteRquest(request);
		return json;
	}

	@IgnoreOperateLogs
	@Operation(summary = "授权的接口信息")
	@Override
	public R<List<WebRequestEntity>> getAuthorisedRequest(List<Integer> menuList) {
		R<List<WebRequestEntity>> json = new R<>();
		if(menuList == null || menuList.isEmpty()) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("未设置页面的id");
			return json;
		}
		return webRequestServiceImpl.getAuthorisedRequest(menuList);
	}
}
