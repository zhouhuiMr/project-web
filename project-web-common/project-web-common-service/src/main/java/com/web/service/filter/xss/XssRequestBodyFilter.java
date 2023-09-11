package com.web.service.filter.xss;

import java.io.IOException;
import java.lang.reflect.Type;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.result.ResultEnum;
import com.web.common.tool.JsoupSafelist;
import com.web.service.handler.error.CustomException;

/**
 * XSS拦截。<br>
 * 只是针对特殊字符进行提醒，并不会进行处理。<br>
 * XSS针对param，可查看{@code XssInterceptorConfigurer}
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
@RestControllerAdvice
public class XssRequestBodyFilter implements RequestBodyAdvice{

	@Autowired
	private ObjectMapper customObjectMapper;
	
	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		XssIgnore xssIgnore = methodParameter.getMethodAnnotation(XssIgnore.class);
		return (xssIgnore == null);
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		String bodyStr = null;
		try {
			bodyStr = customObjectMapper.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(StringUtils.hasText(bodyStr)) {
			String newBodyStr = Jsoup.clean(bodyStr, JsoupSafelist.xssSafelist());
			if(!bodyStr.equals(newBodyStr)) {
				throw new CustomException(ResultEnum.PARAM_HAVE_SPECIAL_CHARACTER.getMessage());
			}
		}
		return body;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

}
