package com.web;

import java.io.File;
import java.net.URL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WorkflowApplication {
	
	public static void main(String[] args) {
		setProjectPath();
		SpringApplication.run(WorkflowApplication.class, args);
	}
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	/**
	 * 获取项目的根目录
	 */
	private static void setProjectPath(){
		String classPathFile = null;
		String projectPath = "";
		ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
		if(classLoader == null) {
			return;
		}
		URL resource = classLoader.getResource("");
		if(resource == null) {
			return;
		}
		classPathFile = classLoader.getResource("").getPath();
		File classpath = new File(classPathFile);
		projectPath = classpath.getParentFile().getParentFile().getParentFile().getPath();
		projectPath = projectPath.replaceFirst("file:", "");
		System.setProperty("projectpath", projectPath);
	}
}
