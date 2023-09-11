package com.web.user.login.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.service.tool.ResponseResultTool;

@Component
public class FormLoginFailureHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		R<Object> json = new R<>();
		json.setResultEnum(ResultEnum.LOGIN_ERROR);
		json.setMessage(exception.getMessage());
		
		ResponseResultTool.setWebResponseJson(response, json);
	}
}
