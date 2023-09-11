package com.web.user.listener;

import java.util.HashMap;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.web.common.tool.ReadContentFromFile;
import com.web.common.user.entity.WebUserEntity;

@Component
public class WebUserRegisterMail {
	
	@Autowired
    private JavaMailSenderImpl mailSender;
	
	@Value("${spring.mail.username}")
	private String mailAddress;

	/**
	 * 构建发送邮件信息的模板
	 * @param tempPath 模板的路径
	 * @param webuser 创建的用户信息
	 * @return MimeMessage 发送邮件的消息
	 *
	 * @author zhouhui
	 * @since 2021.06.22 
	 */
	public MimeMessage buildMailMessage(String tempPath, WebUserEntity webuser) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			helper.setSubject("恭喜 用户注册成功");
			helper.setFrom(mailAddress);
			helper.setTo(webuser.getMail());
			
			HashMap<String, String> data = new HashMap<>();
			data.put("nickName", webuser.getNickname());
			data.put("code", webuser.getCode());
			data.put("password", webuser.getPassword());
			String content = ReadContentFromFile.getContent(tempPath, data);
			
			helper.setText(content, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return helper.getMimeMessage();
	}
}
