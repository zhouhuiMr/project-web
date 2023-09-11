package com.web.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@RestController
@RefreshScope
@Getter
@Setter
public class TokenConfigEntity {
	/**   生成token的密钥       */
	@Value("${jwt.token.key}")
	private String tokenKey = "";
	
	/**   生成token的签名       */
	@Value("${jwt.token.signature}")
	private String tokenSignature = "";
	
	/**   生成token的有效时间，默认一天     */
	@Value("${jwt.token.expire.time}")
	private int tokenExpire = 86400;
	
	/**  head中存放token的名称  */
	@Value("${jwt.token.head.name}")
	private String headTokenName = "Login-Token";
	
}
