package com.web.basic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.web.common.authcode.ImageAuthCode;
import com.web.common.basic.api.AuthCodeApi;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.security.AuthcodeEntity;
import com.web.service.handler.log.IgnoreOperateLogs;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "图片验证码")
@Controller
@RequestMapping(path = "/basic")
public class AuthCodeController implements AuthCodeApi{	
	
	@Autowired
	private ImageAuthCode imageAuthCode;

	@IgnoreOperateLogs
	@Override
	public R<AuthcodeEntity> getImageAuthCode(String sessionId) {
		R<AuthcodeEntity> json = new R<>();
		AuthcodeEntity authCode = imageAuthCode.getAuthCode(sessionId);
		if(authCode == null) {
			json.setResultEnum(ResultEnum.ERROR);
			return json;
		}
		authCode.setAuthcode(null);
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(authCode);
		return json;
	}
}
