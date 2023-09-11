package com.web.common.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "WebRoleEntity对象", description = "用户的角色")
public class WebRoleEntity extends WebRole{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 用户的唯一标识 */
	@Schema(title = "用户的唯一标识")
	private Integer userId;
	
	/** 用户的编号 */
	@Schema(title = "用户的编号")
	private String userCode;
	
	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
