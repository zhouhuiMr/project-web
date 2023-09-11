package com.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import com.web.common.tool.SysDefaultPath;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GatewayApplication {
	
	public static void main(String[] args) {
		SysDefaultPath.setProjectPath();
		SpringApplication.run(GatewayApplication.class, args);
	}
}
