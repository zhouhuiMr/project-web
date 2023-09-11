package com.web.security.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import com.web.common.properties.SecurityLoginEntity;
import com.web.common.properties.ServiceAuthenticationEntity;
import com.web.common.properties.TokenConfigEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUserEntity;
import com.web.config.init.SecurityPermissionInit;
import com.web.tool.GatewayTokenManager;
import com.web.tool.ServerHttpUtil;
import reactor.core.publisher.Mono;


@Component
public class SecurityPermissionFilter implements GlobalFilter,Ordered{
	
	@Autowired
	private ServiceAuthenticationEntity serviceAuthentication;
	
	@Autowired
	private SecurityLoginEntity securityLoginEntity;
	
	@Autowired
	private SecurityPermissionInit permission; 
	
	@Autowired
	private GatewayTokenManager tokenManager;
	
	@Autowired
	private TokenConfigEntity tokenConfig;
	
	private AntPathMatcher matcher = new AntPathMatcher();
	
	/**
	 * 针对用户的权限进行校验。</br>
	 * 1、针对部分地址不进行权限的校验；</br>
	 * 2、进行权限校验：</br>
	 *  1）先判断是否进行了登录；</br>
	 *  2）根据角色判断是否有权限访问此地址；</br>
	 *  3）校验通过之后针对异步操作的功能直接返回处理结果；</br>
	 *  
	 * @param exchange
	 * @param chain
	 * @return Mono<Void>
	 * 
	 * @author zhouhui
	 * @since 2021.05.20
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		
		String[] ignoreWebUrl = serviceAuthentication.getIgnoreWebUrl();
		List<String> ignoreUrlList = new ArrayList<>(Arrays.asList(ignoreWebUrl));
		//添加登录地址
		ignoreUrlList.add(securityLoginEntity.getProcessingUrl());
		
		String requestPath = request.getPath().value();
		
		//不进行权限校验
		for(int i=0;i<ignoreUrlList.size();i++) {
			if(matcher.match(ignoreUrlList.get(i), requestPath)) {
				return chain.filter(exchange);
			}
		}
		
		//需要进行权限校验
		ServerHttpResponse response = exchange.getResponse();
		
		if(permission == null) {
			R<Object> result = new R<>();
			result.setResultEnum(ResultEnum.FORBIDDEN);
			
			return ServerHttpUtil.setResponse(response, result);
		}
		
		/**--   获取token信息   --*/
		List<String> headList = request.getHeaders().get(tokenConfig.getHeadTokenName());
		if(headList == null || headList.size() != 1) {
			R<Object> result = new R<>();
			result.setResultEnum(ResultEnum.FORBIDDEN);
			return ServerHttpUtil.setResponse(response, result);
		}
		String token = headList.get(0);
		
		/** 解析登录用户的信息  */
		R<WebUserEntity> reJson = tokenManager.getUserFromToken(token);
		
		if(!ResultEnum.SUCCESS.getCode().equals(reJson.getCode())) {
			R<Object> result = new R<>();
			result.setCode(reJson.getCode());
			result.setMessage(reJson.getMessage());
			
			return ServerHttpUtil.setResponse(response, result);
		}
		
		/**--   校验token信息是否失效   --*/
		boolean isLoginSuccess = tokenManager.singleLogin(reJson.getData().getUserId(), token);
		if(!isLoginSuccess) {
			R<Object> result = new R<>();
			result.setResultEnum(ResultEnum.TOEKN_EXPIRE);
			
			return ServerHttpUtil.setResponse(response, result);
		}
		
		/**--   判断是否有权限访问此地址   --*/
		boolean isAllow = permission.isAllow(requestPath, reJson.getData());
		if(!isAllow) {
			R<Object> result = new R<>();
			result.setResultEnum(ResultEnum.FORBIDDEN);
			
			return ServerHttpUtil.setResponse(response, result);
		}
		
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return 1;
	}
	
	/**
	 * 获取实际的请求地址，去掉第一位名称，
	 * 例/web/login处理之后为/login
	 * @param requestPath 获取到的url
	 * @return String 权限校验的地址
	 *
	 * @author zhouhui
	 * @since 2021.12.04
	 */
	@SuppressWarnings("unused")
	private String getRealRequestPath(String requestPath) {
		if(!StringUtils.hasText(requestPath)) {
			return "";
		}
		String[] path = requestPath.split("/");
		if(path == null || path.length <= 2) {
			return requestPath;
		}
		if("".equals(path[0])) {
			String webContext = "/" + path[1];
			return requestPath.replaceFirst(webContext, "");
		}else {
			String webContext = "/" + path[0];
			return requestPath.replaceFirst(webContext, "");
		}
	}
}
