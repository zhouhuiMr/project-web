package com.web.common.basic.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.security.AuthcodeEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "imageVerifyApi", path = "/basic")
public interface AuthCodeApi {
	

	/**
	 * 生成对应的图片验证码
	 * @return
	 *
	 * @author zhouhui
	 * @since 2022-05-26
	 */
	@GetMapping(path = "/auth/imageAuthCode")
	@ResponseBody
	R<AuthcodeEntity> getImageAuthCode(@RequestParam("sessionId") String sessionId);
	
}
