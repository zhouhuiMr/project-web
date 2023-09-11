package com.web.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthcodeEntity {
	
	/** 验证时key的名称 */
	@JsonIgnore
	public static final String WEB_SESSION_NAME = "sessionId";
	
	/** 验证时authcode的名称 */
	@JsonIgnore
	public static final String WEB_AUTHCODE_NAME = "authCode";
	
	/** redis存储的key名称 */
	@JsonIgnore
	public static final String AUTHCODE_KEY_NAME = "authCode";
	
	/** 验证码超时时间（秒） */
	@JsonIgnore
	public static final long AUTHCODE_EXPIRE = 1800;

	/** 图片验证码 */
	private String authcode;
	
	/** 缓存对应的key */
	private String sessionId;
	
	/** 图片验证码，对应图片base64 */
	private String imageBase64;
}
