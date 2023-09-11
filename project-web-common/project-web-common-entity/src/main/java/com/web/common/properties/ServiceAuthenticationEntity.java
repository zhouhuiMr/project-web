package com.web.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Controller
@RefreshScope
public class ServiceAuthenticationEntity {
	
	/** 不进行校验的请求地址 */
	@Value("${ignore.web.url}")
	private String[] ignoreWebUrl = {};
	
	/** 登录的首页地址  */
	@Deprecated
	@Value("${web.loginPage:''}")
	private String loginPage = "";
	
	@Deprecated
	@Value("${web.isUseCsrf:false}")
	private boolean isUseCsrf = true;
	
	/** 是否允许跨域访问，true是启用跨域访问校验，false关闭跨域访问校验  */
	@Deprecated
	@Value("${web.isUseCors:false}")
	private boolean isUseCors = true;
	
	/** 设置的cors */
	@Value("${security.cors.origins:''}")
	private String corsOrigins;
	
	/**  白名单IP地址  */
	@Value("${white.list.ip:''}")
	private String[] whiteListIp = {};
}
