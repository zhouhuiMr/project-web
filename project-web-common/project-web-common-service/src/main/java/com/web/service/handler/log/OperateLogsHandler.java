package com.web.service.handler.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.web.common.properties.TokenConfigEntity;
import com.web.common.tool.SysDefaultAdmin;

/**
 * 异步执行操作日志的记录
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class OperateLogsHandler {
	
	/** 远程调用的地址 */
	public static final String HTTP_LOG_PATH = "http://" + ServiceInstanceName.SERVICE_BASIC_NAME + OperateLogConfig.HTTP_LOG_PATH;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private SysDefaultAdmin sysDefaultAdmin;
	
	@Autowired
	private TokenConfigEntity tokenConfig;
	
	@Autowired
	private RestTemplate restTemplateLoadBalanced;

	/**
	 * 异步执行日志信息的存储。
	 * @param logs 日志信息
	 *
	 * @author zhouhui
	 * @since 2023.06.27 
	 */
	@Async("logThreadPoolTask")
	public void saveLogs(BasicOperateLog logs) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		//构建默认的认证信息
		header.set(tokenConfig.getHeadTokenName(), sysDefaultAdmin.createDefaultToken());
		String body = null;
		try {
			body = mapper.writeValueAsString(logs);
			
			HttpEntity<String> httpRequest = new HttpEntity<>(body, header);
			restTemplateLoadBalanced.postForEntity(HTTP_LOG_PATH, httpRequest, Object.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
