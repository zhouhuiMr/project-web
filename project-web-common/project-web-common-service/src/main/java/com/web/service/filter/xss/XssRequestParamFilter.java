package com.web.service.filter.xss;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.JsoupSafelist;
import com.web.service.handler.error.CustomException;
import com.web.service.tool.ResponseResultTool;

/**
 * XSS拦截。<br>
 * 只是针对特殊字符进行提醒，并不会进行处理。<br>
 * XSS针对JSON，可查看{@code XssRequestBodyFilter}<br>
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class XssRequestParamFilter implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HandlerMethod handlerMethod = null;
		if(handler instanceof HandlerMethod) {
			handlerMethod = (HandlerMethod) handler;
			XssIgnore xssIgnore = handlerMethod.getMethodAnnotation(XssIgnore.class);
			if(xssIgnore != null) {
				//忽略特殊字符校验
				return true;
			}
		}
		boolean isCountinue = haveSpecialCharacters(request);
		if(!isCountinue){
			R<Object> json = new R<>();
			json.setResultEnum(ResultEnum.PARAM_HAVE_SPECIAL_CHARACTER);
			ResponseResultTool.setWebResponseJson(response, json);
		}
		return isCountinue;
	}
	
	/**
	 * 判断请求参数中是否包含特殊字符
	 * @param request 请求信息
	 * @return boolean true 正常；false 抛出异常
	 * @throws CustomException
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private boolean haveSpecialCharacters(HttpServletRequest request) {
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String key = paramNames.nextElement();
			String[] values = request.getParameterValues(key);
			if(values == null || values.length == 0) {
				continue;
			}
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if(!StringUtils.hasText(value)) {
					continue;
				}
				String newValue = Jsoup.clean(value, JsoupSafelist.xssSafelist());
				if(!value.equals(newValue)) {
					return false;
				}
			}
		}
		return true;
	}
}
