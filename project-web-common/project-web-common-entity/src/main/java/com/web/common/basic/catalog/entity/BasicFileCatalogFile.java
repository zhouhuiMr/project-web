package com.web.common.basic.catalog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件目录对应的文件
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-16
 */
@Getter
@Setter
@TableName("basic_file_catalog_file")
@Schema(title = "BasicFileCatalogFile对象", description = "文件目录对应的文件")
public class BasicFileCatalogFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;
    
    /** 原文件名称 */
    @Schema(title = "原文件名称")
    private String origFileName;

    /** 文件名称 */
    @Schema(title = "文件名称")
    private String fileName;

    /** 目录的id */
    @Schema(title = "目录的id")
    private Integer catalogId;

    /** 目录路径 */
    @Schema(title = "目录路径")
    private String catalogPath;

    /** 文件类型 */
    @Schema(title = "文件类型")
    private String fileType;

    /** 是否删除，0否；1是 */
    @Schema(title = "是否删除，0否；1是")
    private Integer delFlag;

    /** 创建人 */
    @Schema(title = "创建人")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人 */
    @Schema(title = "修改人")
    private Integer updateUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime updateTime;


}
