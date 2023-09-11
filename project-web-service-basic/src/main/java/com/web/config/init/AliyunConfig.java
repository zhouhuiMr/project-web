package com.web.config.init;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.cms20190101.Client;
import com.aliyun.sdk.service.ecs20140526.AsyncClient;
import com.aliyun.teaopenapi.models.Config;
import com.web.service.aliyun.AliyunConfigEntity;
import darabonba.core.client.ClientOverrideConfiguration;

@Component
public class AliyunConfig {

	@Autowired
	private AliyunConfigEntity aliyunConfigEntity;

	private static final String END_POINT_METRICS = "metrics.cn-beijing.aliyuncs.com";

	private static final String END_POINT_ESC = "ecs.cn-beijing.aliyuncs.com";

	@Bean
	public Client cloudMonitorClient() {
		Config config = new Config();
		config.setEndpoint(END_POINT_METRICS);
		config.setAccessKeyId(aliyunConfigEntity.getAccessKeyId());
		config.setAccessKeySecret(aliyunConfigEntity.getAccessKeySecret());
		config.setConnectTimeout(aliyunConfigEntity.getConnectionTimeout());
		config.setReadTimeout(aliyunConfigEntity.getReadTimeout());
		config.setMaxIdleConns(aliyunConfigEntity.getMaxIdleConnections());
		Client client = null;
		try {
			client = new Client(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}

	@Bean
	public AsyncClient cscClient() {
		StaticCredentialProvider provider = StaticCredentialProvider
				.create(Credential.builder().accessKeyId(aliyunConfigEntity.getAccessKeyId())
						.accessKeySecret(aliyunConfigEntity.getAccessKeySecret()).build());

		ClientOverrideConfiguration config = ClientOverrideConfiguration.create().setEndpointOverride(END_POINT_ESC)
		.setConnectTimeout(Duration.ofMillis(aliyunConfigEntity.getConnectionTimeout()))
		.setResponseTimeout(Duration.ofMillis(aliyunConfigEntity.getReadTimeout()));
		
		return AsyncClient.builder().credentialsProvider(provider)
				.overrideConfiguration(config).build();
	}
}
