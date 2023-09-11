package com.web.common.swagger;

import java.util.ArrayList;
import java.util.List;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * <pre>
 * 问题： 1、swagger-ui界面不显示。
 * 解决办法：https://springdoc.org/#why-am-i-getting-an-error-swagger-ui-unable-to-render-definition-when-overriding-the-default-spring-registered-httpmessageconverter
 * 
 * 文档查询地址 /v3/api-docs
 * @author zhouhui
 * @since 1.0.0
 */
@Configuration
@Profile(value = "dev")
public class SwaggerConfig {

	@Value("${jwt.token.head.name}")
	private String headTokenName = "Login-Token";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private SwaggerConfigEntity swaggerConfigEntity;

	@Bean
	public GroupedOpenApi petApi() {
		return GroupedOpenApi.builder().group(applicationName).pathsToMatch("/**").build();
	}

	@Bean
	public OpenAPI openApi() {
		OpenAPI openApi = new OpenAPI();
		openApi.info(buildApiInfo());
		openApi.externalDocs(new ExternalDocumentation().url(swaggerConfigEntity.getContactUrl()));
		openApi.servers(getServerList());
		openApi.components(new Components()
				.addSecuritySchemes(headTokenName, new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
		return openApi;
	}

	/**
	 * 构建API接口文档信息
	 * 
	 * @return ApiInfo 文档信息
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private Info buildApiInfo() {
		Info info = new Info();
		info.title(swaggerConfigEntity.getSwaggerTitle()).license(new License().name("Apache 2.0")).version("1.0.0")
				.setDescription(applicationName);
		return info;
	}

	/**
	 * 构建接口调用的服务器地址
	 * 
	 * @return List<Server> 接口调用服务器地址
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private List<Server> getServerList() {
		String[] hosts = swaggerConfigEntity.getSwaggerHost();
		if (hosts == null || hosts.length == 0) {
			return new ArrayList<>();
		}
		List<Server> serverList = new ArrayList<>();
		for (int i = 0; i < hosts.length; i++) {
			Server server = new Server();
			server.setUrl(hosts[i]);
			serverList.add(server);
		}
		return serverList;
	}
}
