package com.web.security.filter;

import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.Route.AsyncBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 只在测试系统进行使用，根据配置profiles.active进行注入
 * @author zhouhui
 */
@Profile(value = "dev")
@Component
public class ResetRemoteInstanceFilter implements GlobalFilter,Ordered{
	private static final String VERSION = "VERSION";
	
	@Resource
	private DiscoveryClient discoveryClient;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		List<String> head = request.getHeaders().get(VERSION);
		if(head != null && !head.isEmpty()) {
			String version = head.get(0);
			Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
			if(route != null) {
				Route newRoute = changeRouteByVersion(version, route);
				if(newRoute != null) {
					exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, newRoute);
				}
			}
		}
		return chain.filter(exchange);
	}
	
	@Override
	public int getOrder() {
		return 3;
	}

	/**
	 * 根据请求的VERSION值获取服务地址信息
	 * @param version VERSION值
	 * @param route 路由对象
	 * 
	 * @author zhouhui
	 * @since 2021.11.14
	 */
	private Route changeRouteByVersion(String version,Route route) {
		if(version == null || "".equals(version)) {
			return null;
		}
		URI uri = route.getUri();
		String host = uri.getHost();
		List<ServiceInstance> instancesList = discoveryClient.getInstances(host);
		if(instancesList == null) {
			return null;
		}
		ServiceInstance curInstance = null;
		for(int i=0;i<instancesList.size();i++) {
			ServiceInstance item = instancesList.get(i);
			Map<String, String> metadataMap = item.getMetadata();
			if(metadataMap != null && !metadataMap.isEmpty()) {
				String metadataVersion = metadataMap.get(VERSION);
				if(version.equals(metadataVersion)) {
					curInstance = item;
					break;
				}
			}
		}
		if(curInstance == null) {
			return null;
		}
		AsyncBuilder asyncBuild = Route.async();
		asyncBuild.metadata(route.getMetadata());
		asyncBuild.id(route.getId());
		
		String uriStr = "http://" + curInstance.getHost();
		uriStr += ":" + curInstance.getPort() + "/" + uri.getPath();
		asyncBuild.uri(uriStr);
		
		asyncBuild.filters(route.getFilters());
		asyncBuild.asyncPredicate(route.getPredicate());
		asyncBuild.order(route.getOrder());
		return asyncBuild.build();
	}
}
