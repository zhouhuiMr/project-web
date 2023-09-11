package com.web.service.handler.log;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 日志记录的线程池
 * @author zhouhui
 * @since 1.0.0
 */
@EnableAsync
@Component
public class LogThreadPoolConfig {
	/** 核心线程数 */
	private static final int LOG_THREAD_POOL_CORE_SIZE = 10;

	/** 最大线程数 */
	private static final int LOG_THREAD_POOL_MAX_SIZE = 30;
	
	/** 最大线程数 */
	private static final int LOG_THREAD_POOL_QUEUE = 60;
	
	@Bean
	public ThreadPoolTaskExecutor logThreadPoolTask() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(LOG_THREAD_POOL_CORE_SIZE);
		pool.setMaxPoolSize(LOG_THREAD_POOL_MAX_SIZE);
		pool.setQueueCapacity(LOG_THREAD_POOL_QUEUE);
		pool.setThreadNamePrefix("log-task-");
		return pool;
	}
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplateLoadBalanced() {
		return new RestTemplate();
	}
}
