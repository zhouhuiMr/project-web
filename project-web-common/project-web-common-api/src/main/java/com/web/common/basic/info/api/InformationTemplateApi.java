package com.web.common.basic.info.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.info.entity.MailEntity;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "informationTemplateApi", path = "/basic/info")
public interface InformationTemplateApi {

	/**
	 * 注册成功，发送邮件信息
	 * @param mail 邮件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.12.12
	 */
	@PostMapping(value = "/sendRegisterSuccessMail")
	@ResponseBody
	R<Object> sendRegisterSuccessMail(@RequestBody MailEntity mail);
	
	/**
	 * 重置密码后，发送邮件
	 * @param mail 邮件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.12.13
	 */
	@PostMapping(value = "/resetPasswordMail")
	@ResponseBody
	R<Object> resetPasswordMail(@RequestBody MailEntity mail);
}
