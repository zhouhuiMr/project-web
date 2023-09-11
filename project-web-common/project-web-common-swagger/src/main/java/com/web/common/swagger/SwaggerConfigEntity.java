package com.web.common.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class SwaggerConfigEntity {
	
	/** 接口调用的地址  */
	private String[] swaggerHost = {};

	/** 标题名称  */
	private String swaggerTitle = "";
	
	/** 联系人名称 */
	private String contactName = "";
	
	/** 联系人地址 */
	private String contactUrl = "";
	
	/** 联系人邮箱 */
	private String contactEmail = "";
	
	public String[] getSwaggerHost() {
		return swaggerHost;
	}

	@Value("${swagger.host:}")
	public void setSwaggerHost(String[] swaggerHost) {
		this.swaggerHost = swaggerHost;
	}

	public String getSwaggerTitle() {
		return swaggerTitle;
	}

	@Value("${swagger.title:}")
	public void setSwaggerTitle(String swaggerTitle) {
		this.swaggerTitle = swaggerTitle;
	}

	public String getContactName() {
		return contactName;
	}

	@Value("${swagger.contact.name:}")
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactUrl() {
		return contactUrl;
	}

	@Value("${swagger.contact.url:}")
	public void setContactUrl(String contactUrl) {
		this.contactUrl = contactUrl;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	@Value("${swagger.contact.email:}")
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	
}
