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
 * 部门信息
 * </p>
 *
 * @author zhouhui
 * @since 2023-05-08
 */
@Getter
@Setter
@TableName("web_dept")
@Schema(title = "WebDept对象", description = "部门信息")
public class WebDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 部门唯一标识 */
    @Schema(title = "部门唯一标识")
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Integer deptId;

    /** 部门名称 */
    @NotBlank(message = "未填写部门名称")
    @Schema(title = "部门名称")
    private String deptName;

    /** 父节点的Id */
    @NotNull(message = "未选择上级部门")
    @Schema(title = "父节点的Id")
    private Integer parentId;

    /** 排序，值越大越靠前 */
    @NotNull(message = "未设置排序")
    @Schema(title = "排序，值越大越靠前")
    private Integer sort;

    /** 菜单的状态 默认0，0有效；1无效 */
    @Schema(title = "菜单的状态 默认0，0有效；1无效")
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
