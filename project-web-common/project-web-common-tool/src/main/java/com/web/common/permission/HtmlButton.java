package com.web.common.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(title = "页面中对应按钮")
@Getter
@Setter
public class HtmlButton {
	
	/** 唯一标识,主键 */
	@Schema(title = "唯一标识")
	private Integer requestId = 0;
	
	/** 请求地址 */
	@Schema(title = "请求地址")
	private String requestUrl = "";
	
	/** 按钮名称 */
	@Schema(title = "按钮名称")
	private String btName = "";
	
	/** 按钮中文描述 */
	@Schema(title = "按钮中文描述")
	private String btDescripe = "";
	
	/** 是否允许访问（默认0），0、不允许访问；1、允许访问 */
	@Schema(title = "是否允许访问（默认0），0、不允许访问；1、允许访问")
	private String isAllow = "0";
	
}
