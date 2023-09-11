package com.web.common.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。
 * </p>
 *
 * @author zhouhui
 * @since 2022-09-17
 */
@Getter
@Setter
@Schema(title = "WebRoleRequestEntity对象", description = "角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。")
public class WebRoleRequestEntity extends WebRoleRequest{

    private static final long serialVersionUID = 1L;
    
    /** 菜单的ID */
    @Schema(title = "菜单的ID")
    private Integer menuId;
    
    /** 接口地址 */
    @Schema(title = "接口地址")
    private String requestUrl;
    
    /** 接口地址 */
    @Schema(title = "接口地址")
    private String permissionName;
    
    /** 接口描述 */
    @Schema(title = "接口描述")
    private String btDescribe;
}
