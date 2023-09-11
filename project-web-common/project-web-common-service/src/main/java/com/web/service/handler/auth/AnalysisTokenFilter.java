package com.web.service.handler.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.web.common.properties.SecurityLoginEntity;
import com.web.common.properties.ServiceAuthenticationEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.service.tool.ResponseResultTool;
import com.web.service.tool.WebUserTokenManager;

@Component
public class AnalysisTokenFilter implements Filter{
	
	@Autowired
	private ServiceAuthenticationEntity serviceAuthentication;
	
	@Autowired
	private SecurityLoginEntity securityLoginEntity;
	
	@Autowired
	private WebUserTokenManager webUserTokenManager;
	
	private AntPathMatcher matcher = new AntPathMatcher();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		//在忽略地址列表的不进行用户信息判断
		String[] ignoreWebUrl = serviceAuthentication.getIgnoreWebUrl();
		List<String> ignoreUrlList = new ArrayList<>(Arrays.asList(ignoreWebUrl));
		//添加登录地址
		ignoreUrlList.add(securityLoginEntity.getProcessingUrl());
		
		boolean isIgnore = false;
		for(String ignoreUrl : ignoreUrlList) {
			if(matcher.match(ignoreUrl, req.getServletPath())) {
				isIgnore = true;
				break;
			}
		}
		
		if(!isIgnore) {
			String token = webUserTokenManager.getTokenFromRequest(req);
			if(token == null || "".equals(token)) {
				R<Object> json = new R<>();
				json.setResultEnum(ResultEnum.NO_USER_INFO);
				ResponseResultTool.setWebResponseJson(res, json);
				return;
			}
			
			WebUser user = webUserTokenManager.getUserFromRequest(req);
			if(user == null) {
				R<Object> json = new R<>();
				json.setResultEnum(ResultEnum.NO_USER_INFO);
				ResponseResultTool.setWebResponseJson(res, json);
				return;
			}
			UserUtil.setUser(user);
			chain.doFilter(request, response);
		}else {
			chain.doFilter(request, response);
		}
	}

}
