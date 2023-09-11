package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
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
@TableName("web_role_request")
@Schema(title = "WebRoleRequest对象", description = "角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。")
public class WebRoleRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    @Schema(title = "自增主键")
    @TableId(value = "main_id", type = IdType.AUTO)
    private Integer mainId;

    /** web_request表的唯一标识 web_request表的唯一标识 */
    @NotNull(message = "未设置接口ID")
    @Schema(title = "web_request表的唯一标识 web_request表的唯一标识")
    private Integer requestId;

    /** web_role表的唯一标识 web_role表的唯一标识 */
    @NotNull(message = "未设置角色ID")
    @Schema(title = "web_role表的唯一标识 web_role表的唯一标识")
    private Integer roleId;

    /** 是否允许访问 是否允许访问（默认0），0、不允许访问；1、允许访问 */
    @Schema(title = "是否允许访问 是否允许访问（默认0），0、不允许访问；1、允许访问")
    private String isAllow;

    /** 是否删除。0否；1是 */
    @Schema(title = "是否删除。0否；1是")
    private Integer delFlag;

    /** 创建人的唯一标识 */
    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人的唯一标识 */
    @Schema(title = "修改人的唯一标识")
    private Integer updateUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime updateTime;


}
