package com.web.basic.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.web.basic.service.impl.InformationTemplateServiceImpl;
import com.web.common.basic.info.api.InformationTemplateApi;
import com.web.common.basic.info.entity.MailEntity;
import com.web.common.result.R;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 消息模板 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-11
 */
@Tag(name = "消息模板")
@Controller
@RequestMapping(path = "/basic/info")
public class InformationTemplateController implements InformationTemplateApi{
	
	@Autowired
	private InformationTemplateServiceImpl informationTemplateServiceImpl;

	@Override
	public R<Object> sendRegisterSuccessMail(MailEntity mail) {
		return informationTemplateServiceImpl.sendRegisterSuccessMail(mail);
	}

	@Override
	public R<Object> resetPasswordMail(MailEntity mail) {
		return informationTemplateServiceImpl.resetPasswordMail(mail);
	}


}
