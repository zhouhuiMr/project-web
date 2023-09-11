package com.web.config.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class GatewayConfig {
	
	/**  context path  */
	private String gatewayRouteContextPath = "";

	/**  context path  */
	public String getGatewayRouteContextPath() {
		return gatewayRouteContextPath;
	}

	/**  context path  */
	@Value("${gateway.route.context-path:''}")
	public void setGatewayRouteContextPath(String gatewayRouteContextPath) {
		this.gatewayRouteContextPath = gatewayRouteContextPath;
	}
}
