package com.web.common.basic.notepad.entity;

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
 * 记事本（包含代办事项）
 * </p>
 *
 * @author zhouhui
 * @since 2023-02-07
 */
@Getter
@Setter
@TableName("basic_notepad")
@Schema(title = "BasicNotepad对象", description = "记事本（包含代办事项）")
public class BasicNotepad implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，唯一标识 */
    @Schema(title = "主键，唯一标识")
    @TableId(value = "note_id", type = IdType.AUTO)
    private Integer noteId;

    /** 标题 */
    @NotNull(message = "未设置事件标题")
    @Schema(title = "标题")
    private String title;

    /** 字典项类型，notepad_type */
    @Schema(title = "字典项类型，notepad_type")
    private String type;

    /** 提醒内容 */
    @Schema(title = "提醒内容")
    private String noteMessage;

    /** 是否有效，默认0，0有效，1无效 */
    @Schema(title = "是否有效，默认0，0有效，1无效")
    private Integer delFlag;

    /** 创建日期，格式yyyy-MM-dd */
    @Schema(title = "创建日期，格式yyyy-MM-dd")
    private String createDate;

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
