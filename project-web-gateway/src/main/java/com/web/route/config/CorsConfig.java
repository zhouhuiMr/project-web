package com.web.route.config;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import com.web.common.properties.ServiceAuthenticationEntity;

/**
 * 跨域访问的配置信息
 * 
 * @author zhouhui
 * @since 2021.05.19
 */
@Configuration
public class CorsConfig {
	
	@Bean
	public CorsWebFilter corsFilter(ServiceAuthenticationEntity serviceAuthenticationEntity) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		ArrayList<String> list = new ArrayList<>();
		list.add("*");
		config.setAllowedMethods(list);
		config.setAllowedHeaders(list);
		config.setAllowedOriginPatterns(Arrays.asList(serviceAuthenticationEntity.getCorsOrigins()));
		config.setMaxAge(18000L);
		
		UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource(new PathPatternParser());
		configSource.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(configSource);
	}
	
}
