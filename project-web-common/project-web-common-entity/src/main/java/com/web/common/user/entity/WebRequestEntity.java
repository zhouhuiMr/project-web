package com.web.common.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "WebRequest对象", description = "角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。")
public class WebRequestEntity extends WebRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 菜单名称 */
	@Schema(title = "菜单名称")
	private String menuName;
	
	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小 */
	@Schema(title = "每页大小")
	private int size = 10;

}
