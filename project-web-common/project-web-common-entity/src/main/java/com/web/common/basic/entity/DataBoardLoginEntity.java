package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "用户登录信息看板对象", description = "用户登录信息看板对象")
public class DataBoardLoginEntity {
	
	/** 登录日期 */
	@Schema(title = "登录日期")
	private String loginDate;
	
	/** 登录用户数量 */
	@Schema(title = "登录用户数量")
	private Integer loginUserCount;
}
