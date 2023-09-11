package com.web.common.basic.entity;

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
 * 系统的字典项配置 
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-30
 */
@Getter
@Setter
@TableName("basic_sys_dict")
@Schema(title = "SysDict对象", description = "系统的字典项配置")
public class BasicSysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键唯一标识 */
    @Schema(title = "主键唯一标识")
    @TableId(value = "main_id", type = IdType.AUTO)
    private Integer mainId;

    /** 对应的值 */
    @NotNull(message = "未设置字典值")
    @Schema(title = "对应的值")
    private String value;

    /** 对应值的描述 */
    @NotNull(message = "未设置字典描述")
    @Schema(title = "对应值的描述")
    private String label;

    /** 分组标识 */
    @NotNull(message = "未设置字典类型")
    @Schema(title = "分组标识")
    private String type;

    /** 字段描述 */
    @Schema(title = "字段描述")
    private String description;

    /** 排序 越小优先级越高 */
    @NotNull(message = "未设置排序")
    @Schema(title = "排序 越小优先级越高")
    private Integer sort;

    /** 是否删除，0否；1是 */
    @Schema(title = "是否删除，0否；1是")
    private Integer delFlag;

    /** 创建人的唯一标识 */
    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人的唯一标识 */
    @Schema(title = "修改人的唯一标识")
    private Integer changeUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime changeTime;

}
