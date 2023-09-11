package com.web.common.user.entity;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "WebUserRoleEntity对象", description = "用户对应的角色关系 一个用户可以对应多个角色")
public class WebUserRoleEntity extends WebUserRole{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 角色名称 */
	@Schema(title = "角色名称")
	private String roleName;
	
	/** 授权状态，0未授权；1已授权 */
	@Schema(title = "授权状态，0未授权；1已授权")
	private Integer authorityStatus;
	
	/** 角色列表 */
	@Schema(title = "角色列表")
	private List<Integer> roleList;

	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小 */
	@Schema(title = "每页大小")
	private int size = 10;
}
