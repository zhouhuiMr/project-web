package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 角色对应的菜单和 取消关联关系时可以直接删除数据
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Getter
@Setter
@TableName("web_role_menu")
@Schema(title = "WebRoleMenu对象", description = "角色对应的菜单和 取消关联关系时可以直接删除数据")
public class WebRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    @Schema(title = "自增主键")
    @TableId(value = "main_id", type = IdType.AUTO)
    private Integer mainId;

    /** 角色的唯一标识 */
    @NotNull(message = "未设置角色ID")
    @Schema(title = "角色的唯一标识")
    private Integer roleId;

    /** 菜单的唯一标识 */
    @NotNull(message = "未设置菜单ID")
    @Schema(title = "菜单的唯一标识")
    private Integer menuId;

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

    /** 是否有效，默认0，0有效，1无效 */
    @Schema(title = "是否有效，默认0，0有效，1无效")
    private Integer delFlag;

    /** 父菜单的唯一标识 */
    @TableField(exist = false)
    @Schema(title = "父菜单的唯一标识")
    private Integer parentId;
}
