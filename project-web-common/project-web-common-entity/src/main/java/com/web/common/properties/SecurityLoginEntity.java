package com.web.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.Setter;

@Controller
@ConfigurationProperties(prefix = "security.login")
@Getter
@Setter
public class SecurityLoginEntity {
	
	/** 登录页面地址 */
	private String page;
	
	/** 登录接口地址 */
	private String processingUrl;
}
