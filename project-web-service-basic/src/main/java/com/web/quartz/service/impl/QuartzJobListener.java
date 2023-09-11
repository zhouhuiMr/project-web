package com.web.quartz.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.quartz.entity.QuartzJobNotify;
import com.web.common.event.QuartzRefreshEvent;
import com.web.common.properties.TokenConfigEntity;
import com.web.common.result.R;
import com.web.common.tool.SysDefaultAdmin;

/**
 * 通过HTTP请求通知此服务的所有负载。
 * 也可以通过其他中间件进行消息的通知，比如rocketMQ等进行处理。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class QuartzJobListener implements ApplicationListener<QuartzRefreshEvent> {
	
	private static final String NOTIFY_QUARTZ_JOB = "/basic/quartz/notify";

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private RestTemplate httpTemplate;
	
	@Autowired
	private TokenConfigEntity tokenConfig;
	
	@Autowired
	private SysDefaultAdmin sysDefaultAdmin;
	
	@Override
	public void onApplicationEvent(QuartzRefreshEvent event) {
		List<ServiceInstance> instanceList = discoveryClient.getInstances(ServiceInstanceName.SERVICE_BASIC_NAME);
		if(instanceList == null || instanceList.isEmpty()) {
			return;
		}
		
		for(ServiceInstance item: instanceList) {
			String url = "http://" + item.getHost() + ":" + item.getPort() + NOTIFY_QUARTZ_JOB;
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			header.set(tokenConfig.getHeadTokenName(), sysDefaultAdmin.createDefaultToken());
			
			QuartzJobNotify notify = new QuartzJobNotify();
			notify.setExecIdList(event.getExecIdList());
			notify.setOperateType(event.getOperateType());
			HttpEntity<QuartzJobNotify> httpEntity = new HttpEntity<>(notify, header);
			
			try {
				httpTemplate.exchange(url, HttpMethod.POST, httpEntity, R.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
