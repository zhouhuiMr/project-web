package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户对应的部门信息
 * </p>
 *
 * @author zhouhui
 * @since 2023-05-08
 */
@Getter
@Setter
@TableName("web_user_dept")
@Schema(title = "WebUserDept对象", description = "用户对应的部门信息")
public class WebUserDept implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /** 用户唯一标识 */
    @Schema(title = "用户唯一标识")
    private Integer userId;

    /** 部门唯一标识 */
    @Schema(title = "部门唯一标识")
    private Integer deptId;

    /** 是否删除 默认0，0否；1是。 */
    @Schema(title = "是否删除 默认0，0否；1是。")
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
