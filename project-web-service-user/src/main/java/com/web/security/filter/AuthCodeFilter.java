package com.web.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.web.common.authcode.ImageAuthCode;
import com.web.common.properties.SecurityLoginEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.security.AuthcodeEntity;
import com.web.service.tool.ResponseResultTool;

@Component
@Order(2)
public class AuthCodeFilter implements Filter {
	
	@Autowired
	private SecurityLoginEntity securityLoginEntity;
	
	@Autowired
	private ImageAuthCode imageAuthCode;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String url = req.getRequestURI();
		
		if(securityLoginEntity.getProcessingUrl().equals(url)) {
			String authCode = request.getParameter(AuthcodeEntity.WEB_AUTHCODE_NAME);
			String sessionId = request.getParameter(AuthcodeEntity.WEB_SESSION_NAME);
			if(!StringUtils.hasText(authCode) || !StringUtils.hasText(sessionId)) {
				R<Object> json = new R<>();
				json.setResultEnum(ResultEnum.AUTHCODE_ERROR);
				ResponseResultTool.setWebResponseJson(res, json);
				return;
			}
			AuthcodeEntity authcodeEntity = new AuthcodeEntity();
			authcodeEntity.setSessionId(sessionId);
			authcodeEntity.setAuthcode(authCode);
			boolean isSuccess = imageAuthCode.verifyAuthCode(authcodeEntity);
			if(!isSuccess) {
				R<Object> json = new R<>();
				json.setResultEnum(ResultEnum.AUTHCODE_ERROR);
				ResponseResultTool.setWebResponseJson(res, json);
				return;
			}
		}
		chain.doFilter(request, response);
	}

}
