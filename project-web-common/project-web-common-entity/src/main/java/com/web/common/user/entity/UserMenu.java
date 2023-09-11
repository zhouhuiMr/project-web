package com.web.common.user.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "登录用户对应的菜单", description = "登录用户对应的菜单")
public class UserMenu implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(title = "唯一标识 主键")
    private Integer menuId;
	
	@Schema(title = "菜单的索引值")
	private String index;
	
	@Schema(title = "菜单名称")
    private String menuName;

    @Schema(title = "菜单的顺序 菜单的顺序编号，排序从小到大进行排序")
    private Integer menuOrder;

    @Schema(title = "菜单对应的图标地址，或者图标信息")
    private String icon;

    @Schema(title = "菜单访问地址")
    private String menuUrl;
    
    @Schema(title = "菜单的适用范围 默认0，0客户端使用；1管理端使用")
    private String menuScope;

    @Schema(title = "菜单的类型 默认0，0、当前页面进行展示；1、新的页签进行展示")
    private String menuType;

    @Schema(title = "菜单等级 默认1级，一级菜单，编号1、2、3......")
    private Integer menuLevel;
    
    @Schema(title = "父菜单的唯一标识")
    private Integer parentId;
    
    @Schema(title = "子菜单")
    private List<UserMenu> children = new ArrayList<>();
}
