package com.web.spring.tool;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanUtil implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	/**
	 * 根据Bean的名称获取对应的Bean
	 * @param beanName bean名称
	 * @return Object Bean对象
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}
}
