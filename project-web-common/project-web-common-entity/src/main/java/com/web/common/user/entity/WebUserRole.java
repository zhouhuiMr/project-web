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
 * 用户对应的角色关系 一个用户可以对应多个角色
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Getter
@Setter
@TableName("web_user_role")
@Schema(title = "WebUserRole对象", description = "用户对应的角色关系 一个用户可以对应多个角色")
public class WebUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "自增主键")
    @TableId(value = "main_id", type = IdType.AUTO)
    private Integer mainId;

    @NotNull(message = "请选择用户")
    @Schema(title = "用户的唯一标识")
    private Integer userId;

    @NotNull(message = "请选择角色")
    @Schema(title = "角色的唯一标识")
    private Integer roleId;

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
