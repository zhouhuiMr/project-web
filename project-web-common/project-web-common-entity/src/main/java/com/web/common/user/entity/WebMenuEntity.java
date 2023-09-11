package com.web.common.user.entity;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "WebMenuEntity对象", description = "菜单信息")
public class WebMenuEntity extends WebMenu{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 菜单等级描述 默认1级，一级菜单，编号1、2、3...... */
	@Schema(title = "菜单等级描述 默认1级，一级菜单，编号1、2、3......")
	private String menuLevelStr;
	
	/** 菜单的适用范围 默认0，0客户端使用；1管理端使用 */
	@Schema(title = "菜单的适用范围 默认0，0客户端使用；1管理端使用")
    private String menuScopeStr;
	
	/** 菜单的类型 默认0，0、当前页面进行展示；1、新的页签进行展示 */
    @Schema(title = "菜单的类型 默认0，0、当前页面进行展示；1、新的页签进行展示")
	private String menuTypeStr;
    
    /** 是否有权限访问（默认0），0、不允许访问；1、允许访问 */
    @Schema(title = "是否有权限访问（默认0），0、不允许访问；1、允许访问")
    private String isAllow;
	
	/** 子菜单列表 */
	@Schema(title = "子菜单列表")
	private List<WebMenuEntity> children;
	
	/** 父菜单名称 */
	@Schema(title = "父菜单名称")
	private String parentName;
	
	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
