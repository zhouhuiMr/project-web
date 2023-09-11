package com.web.service.handler.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.web.common.properties.ServiceAuthenticationEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;

/**
 * 记录操作日志信息。<br>
 * 1、没有将记录放在Gateway处理，主要因为获取返回信息困难；<br>
 * 2、日志只是拦截了@ResponseBody标记的controller；<br>
 * 3、controller添加@IgnoreOperateLogs则不会进行日志记录；<br>
 * 4、日志记录为调用记录日志的服务，已将请求请求地址排除，防止循环调用；<br>
 * 5、存在调用本身服务项目的请求，为了统一逻辑和方便处理。<br>
 * 
 * 注：如果controller添加@IgnoreOperateLogs 但是接口报错之后还是会进行记录！
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@RestControllerAdvice
public class OperateLogsAdvice implements ResponseBodyAdvice<Object>, Ordered{
	
	@Autowired
	private OperateLogsHandler operateLogsHandler;
	
	@Autowired
	private ServiceAuthenticationEntity serviceAuthentication;
	
	private AntPathMatcher matcher = new AntPathMatcher();

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		IgnoreOperateLogs ignoreLogs = returnType.getMethodAnnotation(IgnoreOperateLogs.class);
		return ignoreLogs == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		//排除日志记录的地址，防止循环调用记录
		String requestPath = request.getURI().getPath();
		if(OperateLogConfig.HTTP_LOG_PATH.equals(requestPath)) {
			return body;
		}
		
		//需要忽略的地址
		String[] ignoreWebUrl = serviceAuthentication.getIgnoreWebUrl();
		List<String> ignoreUrlList = new ArrayList<>(Arrays.asList(ignoreWebUrl));
		for(String ignoreUrl : ignoreUrlList) {
			if(matcher.match(ignoreUrl, requestPath)) {
				return body;
			}
		}
		
		//获取请求的信息
		String requestMethod = null;
		HttpMethod method = request.getMethod();
		if(method != null) {
			requestMethod = method.toString();
		}
		String serviceName = getServiceName(requestPath);
		
		String requestContentType = null;
		MediaType contentType = request.getHeaders().getContentType();
		if(contentType != null) {
			requestContentType = contentType.toString();
			if(requestContentType.indexOf(MediaType.MULTIPART_FORM_DATA_VALUE) >= 0) {
				requestContentType = MediaType.MULTIPART_FORM_DATA_VALUE;
			}
		}
		
		//组装日志信息
		BasicOperateLog logs = new BasicOperateLog();
		WebUser user = UserUtil.getUser();
		logs.setPath(requestPath);
		logs.setServiceName(serviceName);
		logs.setRequestMethod(requestMethod);
		logs.setContentType(requestContentType);
		if(user != null) {
			logs.setUserId(user.getUserId());
			logs.setUserCode(user.getCode());
			logs.setUserNickname(user.getNickname());
		}
		if(body instanceof R) {
			R<Object> result = (R<Object>) body;
			logs.setResultCode(result.getCode());
			logs.setResultMessage(result.getMessage());
		}
		if(body instanceof byte[]) {
			//下载时返回字节流，默认为成功
			logs.setResultCode(ResultEnum.SUCCESS.getCode());
			logs.setResultMessage(ResultEnum.SUCCESS.getMessage());
		}
		if(response instanceof ServletServerHttpResponse) {
			ServletServerHttpResponse res = (ServletServerHttpResponse) response;
			logs.setHttpStatus(res.getServletResponse().getStatus());
		}
		operateLogsHandler.saveLogs(logs);
		
		return body;
	}
	
	/**
	 * 获取服务名称
	 * @param requestPath 请求地址
	 * @return String 服务的名称
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private String getServiceName(String requestPath) {
		if(!StringUtils.hasText(requestPath)) {
			return "";
		}
		String[] pathArray = requestPath.split("/");
		if(requestPath.startsWith("/")) {
			if(pathArray != null && pathArray.length >= 2) {
				return pathArray[1];
			}
		}else {
			if(pathArray != null && pathArray.length >= 1) {
				return pathArray[0];
			}
		}
		return "";
	}

	@Override
	public int getOrder() {
		return -1;
	}

}
