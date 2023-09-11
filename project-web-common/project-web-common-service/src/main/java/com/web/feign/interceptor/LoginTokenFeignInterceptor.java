package com.web.feign.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import com.web.common.properties.TokenConfigEntity;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign请求设置登录信息。
 * @author zhouhui
 * @since 1.0.0
 */
public class LoginTokenFeignInterceptor implements RequestInterceptor{
	
	@Autowired
	private HttpServletRequest httpServletRequest;
	
	@Autowired
	private TokenConfigEntity tokenConfig;

	@Override
	public void apply(RequestTemplate template) {
		String token = httpServletRequest.getHeader(tokenConfig.getHeadTokenName());
		template.header(tokenConfig.getHeadTokenName(), token);
	}

}
