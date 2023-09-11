package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Getter
@Setter
@TableName("web_request")
@Schema(title = "WebRequest对象", description = "角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。")
public class WebRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 唯一标识 主键 */
    @Schema(title = "唯一标识 主键")
    @TableId(value = "request_id", type = IdType.AUTO)
    private Integer requestId;

    /** 菜单的唯一标识 菜单的唯一标识 */
    @NotNull(message = "未选择对应的菜单")
    @Schema(title = "菜单的唯一标识 菜单的唯一标识")
    private Integer menuId;

    /** 请求地址 请求地址 */
    @NotBlank(message = "未设置请求地址")
    @Schema(title = "请求地址 请求地址")
    private String requestUrl;

    /** 权限标识名称 */
    @Schema(title = "权限标识名称")
    private String permissionName;

    /** 按钮中文描述 按钮中文描述 */
    @Schema(title = "按钮中文描述 按钮中文描述")
    private String btDescribe;

    /** 是否删除（默认0）。0否；1是。 */
    @Schema(title = "是否删除（默认0）。0否；1是。")
    private Integer delFlag;

    /** 创建人的唯一标识 创建人的唯一标识 */
    @Schema(title = "创建人的唯一标识 创建人的唯一标识")
    private Integer createUserId;

    /** 创建时间 创建时间 */
    @Schema(title = "创建时间 创建时间")
    private LocalDateTime createTime;

    /** 修改人唯一标识 */
    @Schema(title = "修改人唯一标识")
    private Integer updateUserId;

    /** 修改时间 修改时间 */
    @Schema(title = "修改时间 修改时间")
    private LocalDateTime updateTime;

}
