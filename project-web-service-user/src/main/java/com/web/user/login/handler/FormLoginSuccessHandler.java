package com.web.user.login.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.web.common.properties.SecurityLoginEntity;
import com.web.common.properties.TokenConfigEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.security.AuthenticationEntity;
import com.web.common.user.entity.WebUserEntity;
import com.web.service.handler.log.OperateLogsHandler;
import com.web.service.tool.ResponseResultTool;
import com.web.service.tool.WebUserTokenManager;
import com.web.user.service.impl.WebUserServiceImpl;

@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler{
	
	@Autowired
	private TokenConfigEntity tokenConfig;
	
	@Autowired
	private RedisTemplate<String,String> template;
	
	@Autowired
	private WebUserServiceImpl webUserServiceImpl;
	
	@Autowired
	private WebUserTokenManager tokenManager;
	
	@Autowired
	private OperateLogsHandler operateLogsHandler;
	
	@Autowired
	private SecurityLoginEntity securityLoginEntity;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		User principal = (User) authentication.getPrincipal();
		
		R<Object> json = new R<>();
		
		//将用户信息存储redis	
		WebUserEntity webUser = storeWebUserToRedist(principal);
		if(webUser == null) {
			json.setResultEnum(ResultEnum.ERROR);
			ResponseResultTool.setWebResponseJson(response, json);
			return;
		}
		
		//设置head
		response.setHeader(tokenConfig.getHeadTokenName(), webUser.getToken());
		
		//记录日志
		saveLogs(webUser);
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(webUser);
		ResponseResultTool.setWebResponseJson(response, json);
	}

	/**
	 * 将用户信息和授权信息存入redis
	 * @param principal 用户的信息
	 * @return WebUser 用户的信息
	 *
	 * @author zhouhui
	 * @since 2021.11.28
	 */
	private WebUserEntity storeWebUserToRedist(User principal) {
		R<WebUserEntity> result = webUserServiceImpl.getUserInfo(principal.getUsername());
		if(result == null || !ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
			return null;
		}
		WebUserEntity user = result.getData();
		
		//user to json 
		ObjectMapper mapper = new ObjectMapper();
		String userJson = null;
		try {
			userJson = mapper.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(userJson == null) {
			return null;
		}
		
		//create JWT
		String token = tokenManager.createToken(user);
		user.setToken(token);
		
		//store user message
		String redisUserKey = AuthenticationEntity.getUserKey(user.getUserId().toString());
		HashMap<String, String> dataMap = new HashMap<>();
		dataMap.put(AuthenticationEntity.USER_KEY_TOKEN, token);
		dataMap.put(AuthenticationEntity.USER_KEY_INFO, userJson);
		template.opsForHash().putAll(redisUserKey, dataMap);
		template.opsForHash().getOperations().expire(redisUserKey, tokenConfig.getTokenExpire(), TimeUnit.SECONDS);
		
		return user;
	}
	
	/**
	 * 记录操作日志信息
	 * @param webUser 用户信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private void saveLogs(WebUserEntity webUser) {
		BasicOperateLog logs = new BasicOperateLog();
		logs.setPath(securityLoginEntity.getProcessingUrl());
		logs.setServiceName("user");
		logs.setRequestMethod("POST");
		logs.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		logs.setUserId(webUser.getUserId());
		logs.setUserCode(webUser.getCode());
		logs.setHttpStatus(HttpStatus.OK.value());
		logs.setResultCode(ResultEnum.SUCCESS.getCode());
		logs.setResultMessage(ResultEnum.SUCCESS.getMessage());
		operateLogsHandler.saveLogs(logs);
	}
}
