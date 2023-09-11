package com.web.common.basic.info.entity;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 邮件信息
 * @author zhouhui
 * @since 1.0.0
 */
@Getter
@Setter
public class MailEntity {
	
	/** 发件地址 */
	private String fromMailAddress;
	
	/** 收件地址列表 */
	private List<String> toMailAddress;
	
	/** 邮件内容的数据 */
	private Map<String, String> dataMap;
	
	/** 邮件主题 */
	private String subject;
	
	/** 模板类型 */
	private String templateType;
	
	/** 模板编号 */
	private String templateCode;
	
	/** 邮件内容 */
	private String mailMessage;
}
