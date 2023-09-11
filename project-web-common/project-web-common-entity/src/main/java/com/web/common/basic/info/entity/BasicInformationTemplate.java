package com.web.common.basic.info.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 消息模板
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-11
 */
@Getter
@Setter
@TableName("basic_information_template")
@Schema(title = "BasicInformationTemplate对象", description = "消息模板")
public class BasicInformationTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "template_id", type = IdType.AUTO)
    private Integer templateId;

    /** 模板类型 */
    @Schema(title = "模板类型")
    private String templateType;

    /** 模板编号 */
    @Schema(title = "模板编号")
    private String templateCode;
    
    /** 消息主题 */
    @Schema(title = "消息主题")
    private String subject;

    /** 模板信息 */
    @Schema(title = "模板信息")
    private String templateInformation;

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
