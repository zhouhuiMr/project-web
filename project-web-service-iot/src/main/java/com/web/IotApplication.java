package com.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import com.web.common.tool.SysDefaultPath;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class IotApplication {
	
	public static void main(String[] args) {
		SysDefaultPath.setProjectPath();
		SpringApplication.run(IotApplication.class, args);
	}
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
