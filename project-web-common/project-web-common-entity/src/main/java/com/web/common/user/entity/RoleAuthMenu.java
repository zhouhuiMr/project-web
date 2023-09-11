package com.web.common.user.entity;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleAuthMenu {
	
	/** 唯一标识 主键 */
    @Schema(title = "唯一标识 主键")
    private Integer menuId;

    /** 菜单名称 */
    @Schema(title = "菜单名称")
    private String menuName;
    
    /** 菜单对应的图标地址，或者图标信息 */
    @Schema(title = "菜单对应的图标地址，或者图标信息")
    private String icon;

    /** 菜单访问地址 */
    @Schema(title = "菜单访问地址")
    private String menuUrl;

    /** 父菜单的唯一标识 */
    @Schema(title = "父菜单的唯一标识")
    private Integer parentId;
    
    /** 是否有权限访问（默认0），0、不允许访问；1、允许访问 */
    @Schema(title = "是否有权限访问（默认0），0、不允许访问；1、允许访问")
    private String isAllow;
    
    /** 角色唯一标识 */
    @Schema(title = "角色唯一标识")
    private Integer roleId;
    
    /** 对应的子菜单 */
    @Schema(title = "对应的子菜单")
    private List<RoleAuthMenu> children;
}
