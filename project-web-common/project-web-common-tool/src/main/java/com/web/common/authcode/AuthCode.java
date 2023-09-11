package com.web.common.authcode;

import com.web.common.security.AuthcodeEntity;

/**
 * 生成验证码
 * @author zhouhui
 */
public interface AuthCode {

	/**
	 * 创建验证码
	 * @param cookieValue cookie值
	 * @return AuthcodeEntity 验证码信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	AuthcodeEntity getAuthCode(String cookieValue);
	
	/**
	 * 校验验证码是否正确。
	 * 注：此方法可能存在并发情况，如果需要控制则在调用时进行控制。
	 * @param entity 验证码信息
	 * @return boolean true验证码正确；false验证码错误
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	boolean verifyAuthCode(AuthcodeEntity entity);
}
