package com.web.service.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.web.service.filter.resubmit.ResubmitHandler;
import com.web.service.filter.xss.XssRequestParamFilter;

/**
 * 配置Spring的intercepter拦截器
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class WebInterceptorConfigurer implements WebMvcConfigurer{
	
	@Autowired
	private XssRequestParamFilter xssRequestParamFilter;
	
	@Autowired
	private ResubmitHandler resubmitHandler;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(resubmitHandler);
		registry.addInterceptor(xssRequestParamFilter);
	}
}
