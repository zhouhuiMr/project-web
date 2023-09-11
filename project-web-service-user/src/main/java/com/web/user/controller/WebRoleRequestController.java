package com.web.user.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.api.RoleRequestApi;
import com.web.common.user.entity.WebRoleRequest;
import com.web.common.user.entity.WebRoleRequestEntity;
import com.web.user.service.WebRoleRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2022-09-17
 */
@Tag(name = "角色与接口对应关系")
@Controller
@RequestMapping(path = "/user/role/request")
public class WebRoleRequestController implements RoleRequestApi{
	
	
	@Autowired
	private WebRoleRequestService webRoleRequestService;

	@Operation(summary = "根据角色获取接口")
	@Override
	public R<List<WebRoleRequestEntity>> getAuthRequest(WebRoleRequestEntity condition) {
		return webRoleRequestService.getAuthRequest(condition);
	}

	@Operation(summary = "保存或者更新角色对应接口的关系")
	@Override
	public R<Object> saveOrUpdateRoleRequest(@Valid WebRoleRequest roleRequest) {
		R<Object> json = new R<>();
		if(!StringUtils.hasText(roleRequest.getIsAllow())) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("设置接口是否允许访问");
			return json;
		}
		if(!"0".equals(roleRequest.getIsAllow()) && !"1".equals(roleRequest.getIsAllow()) ) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		return webRoleRequestService.saveOrUpdateRoleRequest(roleRequest);
	}

}
