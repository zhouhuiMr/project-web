package com.web.service.filter.resubmit;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.Symbols;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.service.tool.ResponseResultTool;

/**
 * 重复提交拦截，
 * 将数据据存储到redis，key(userId):url
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class ResubmitHandler implements HandlerInterceptor{
	
	private static final String RESUBMIT_KEY = "resubmit::";
	
	@Autowired
	private RedisTemplate<String, String> template;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean isCanSubmit = true;
		HandlerMethod handlerMethod = null;
		if(handler instanceof HandlerMethod) {
			handlerMethod = (HandlerMethod) handler;
			Resubmit resubmit = handlerMethod.getMethodAnnotation(Resubmit.class);
			if(resubmit == null) {
				return true;
			}
			isCanSubmit = isCanSubmit(request, resubmit);
			if(!isCanSubmit) {
				R<Object> json = new R<>();
				json.setResultEnum(ResultEnum.SUBMIT_AGAIN_ERROR);
				ResponseResultTool.setWebResponseJson(response, json);
			}
		}
		return isCanSubmit;
	}
	
	/**
	 * 是否可以进行提交请求信息
	 * @param request 请求信息
	 * @return boolean true可以提交；false不能提交
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private boolean isCanSubmit(HttpServletRequest request, Resubmit resubmit) {
		WebUser user = UserUtil.getUser();
		if(user == null) {
			return false;
		}
		Integer userId = user.getUserId();
		String path = request.getServletPath();
		
		String key = RESUBMIT_KEY + userId + Symbols.COLON + path;
		String value = template.opsForValue().get(key);
		if(value == null) {
			template.opsForValue().set(key, "1", resubmit.interval(), TimeUnit.SECONDS);
		}else {
			return false;
		}
		return true;
	}
}
