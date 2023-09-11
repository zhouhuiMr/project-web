package com.web.common.user.entity;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "WebUserEntity对象", description = "用户信息")
public class WebUserEntity extends WebUser{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 登录信息的session值  */
	@TableField(exist = false)
	@Schema(title = "登录信息的session值")
	private String sessionId = null;
	
	/** 验证码（图片验证码（目前），短信验证码） */
	@TableField(exist = false)
	@Schema(title = "验证码（图片验证码（目前），短信验证码）")
	private String authCode = null;
	
	/** 用户对应的角色列表 */
	@TableField(exist = false)
	@Schema(title = "用户对应的角色列表")
	private ArrayList<WebRoleEntity> roleList = new ArrayList<>();
	
	/** 角色的唯一标识列表  */
	@TableField(exist = false)
	@Schema(title = "角色的唯一标识列表")
	private ArrayList<Integer> roles = new ArrayList<>();
	
	/** 登录用户生成的身份令牌 */
	@TableField(exist = false)
	@Schema(title = "登录用户生成的身份令牌")
	private String token = null;
	
	/** 旧的密码 */
	@TableField(exist = false)
	@Schema(title = "旧的密码")
	private String oldPassword;
	
	/** 修改之后新的密码 */
	@TableField(exist = false)
	@Schema(title = "修改之后新的密码")
	private String newPassword;
	
	/** 所属部门列表 */
	@TableField(exist = false)
	@Schema(title = "所属部门Id列表")
	private List<Integer> deptIdList;
	
	/** 多部门Id */
	@TableField(exist = false)
	@Schema(title = "多部门名Id")
	private String deptIds;
	
	/** 多部门名称 */
	@TableField(exist = false)
	@Schema(title = "多部门名称")
	private String deptNames;
	
	/** 当前页数 */
	@TableField(exist = false)
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小 */
	@TableField(exist = false)
	@Schema(title = "每页大小")
	private int size = 10;
}
