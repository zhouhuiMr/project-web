package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户的角色
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Getter
@Setter
@TableName("web_role")
@Schema(title = "WebRole对象", description = "用户的角色")
public class WebRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "唯一标识 自增")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    @NotEmpty(message = "未设置角色名称")
    @Schema(title = "角色名称 描述")
    private String roleName;

    @Schema(title = "是否删除 默认0，0否；1是")
    private Integer delFlag;

    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "修改人的唯一标识")
    private Integer updateUserId;

    @Schema(title = "修改时间")
    private LocalDateTime updateTime;
}
