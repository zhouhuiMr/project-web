package com.web.basic.service.impl;

import com.web.basic.mapper.InformationTemplateMapper;
import com.web.common.basic.info.entity.BasicInformationTemplate;
import com.web.common.basic.info.entity.MailEntity;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.ReadContentFromFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 消息模板 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-11
 */
@Service
public class InformationTemplateServiceImpl extends ServiceImpl<InformationTemplateMapper, BasicInformationTemplate> {
	
	/** 用户保存成功发送邮件模板 */
	private static final String TEMPLATE_REGISTER_SUCCESS = "REGISTER_SUCCESS";
	
	/** 重置密码发送邮件的模板 */
	private static final String TEMPLATE_RESET_PASSWORD = "RESET_PASSWORD";
	
	@Value("${spring.mail.username}")
	private String mailAddress;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	
	/**
	 * 发送用户注册成功的邮件信息
	 * @param mail 邮件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.12.12
	 */
	public R<Object> sendRegisterSuccessMail(MailEntity mail){
		mail.setTemplateType(TEMPLATE_REGISTER_SUCCESS);
		return sendTextMail(mail);
	}
	
	public R<Object> resetPasswordMail(MailEntity mail){
		mail.setTemplateType(TEMPLATE_RESET_PASSWORD);
		return sendTextMail(mail);
	}
	
	/**
	 * 发送文本邮件。
	 * 1、根据模板类型和模板编号获取模板内容；
	 * 2、创建发送邮件的内容；
	 * 3、发送邮件内容。
	 * @param mail 邮件对象信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.12.12
	 */
	public R<Object> sendTextMail(MailEntity mail){
		R<Object> json = new R<>();
		if(!StringUtils.hasText(mail.getTemplateType()) && !StringUtils.hasText(mail.getTemplateCode())) {
			json.setResultEnum(ResultEnum.INFO_MAIL_NO_TEMPLATE);
			return json;
		}
		
		//获取模板内容
		LambdaQueryWrapper<BasicInformationTemplate> query = new LambdaQueryWrapper<>();
		if(StringUtils.hasText(mail.getTemplateType())) {
			query.eq(BasicInformationTemplate::getTemplateType, mail.getTemplateType());
		}
		if(StringUtils.hasText(mail.getTemplateCode())) {
			query.eq(BasicInformationTemplate::getTemplateType, mail.getTemplateCode());
		}
		query.eq(BasicInformationTemplate::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<BasicInformationTemplate> templateList = baseMapper.selectList(query);
		if(templateList == null || templateList.isEmpty()) {
			json.setResultEnum(ResultEnum.INFO_MAIL_NO_TEMPLATE);
			return json;
		}
		BasicInformationTemplate template = templateList.get(0);
		mail.setMailMessage(template.getTemplateInformation());
		mail.setSubject(template.getSubject());
		
		MimeMessage mimeMessage = createTextMailContent(mail);
		try {
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			json.setResultEnum(ResultEnum.INFO_MAIL_SEND_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 创建要发送的邮件内容。
	 * dataMap中的数据会通过占位符（${key}）进行替换。
	 * @param mail 生成邮件的主要参数
	 * @return R<Object> 发送结果
	 *
	 * @author zhouhui
	 * @since 2022.12.11
	 */
	public MimeMessage createTextMailContent(MailEntity mail){
		if(mail.getToMailAddress() == null || mail.getToMailAddress().isEmpty()) {
			return null;
		}
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			helper.setSubject(mail.getSubject());
			if(StringUtils.hasText(mail.getFromMailAddress())) {
				helper.setFrom(mail.getFromMailAddress());
			}else {
				helper.setFrom(mailAddress);
			}
			String[] toAddresses = mail.getToMailAddress().toArray(new String[mail.getToMailAddress().size()]);
			helper.setTo(toAddresses);
			
			String content = ReadContentFromFile.replaceChar(mail.getMailMessage(), mail.getDataMap());
			helper.setText(content, true);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mimeMessage;
	} 
}
