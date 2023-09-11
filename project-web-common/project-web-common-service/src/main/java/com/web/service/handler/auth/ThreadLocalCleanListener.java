package com.web.service.handler.auth;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * 清除ThreadLocal中的数据。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class ThreadLocalCleanListener implements ApplicationListener<ServletRequestHandledEvent> {

	@Override
	public void onApplicationEvent(ServletRequestHandledEvent event) {
		UserUtil.cleanThreadLocal();
	}

}
