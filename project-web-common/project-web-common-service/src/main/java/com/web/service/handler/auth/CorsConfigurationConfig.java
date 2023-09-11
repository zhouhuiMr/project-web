package com.web.service.handler.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.web.common.properties.ServiceAuthenticationEntity;

@Component
public class CorsConfigurationConfig {
	
	@Autowired
	private ServiceAuthenticationEntity serviceAuthenticationEntity;

	@Profile(value = {"dev"})
	@Bean(name = "selfCorsConfigurationSource")
	private CorsConfigurationSource devCorsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(serviceAuthenticationEntity.getCorsOrigins()));
		configuration.setAllowedMethods(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Profile(value = {"prd"})
	@Bean(name = "selfCorsConfigurationSource")
	private CorsConfigurationSource prdCorsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(serviceAuthenticationEntity.getCorsOrigins()));
		configuration.setAllowedMethods(Arrays.asList("POST", "GET"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
