package com.web.common.basic.catalog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.web.common.dictionary.VerifyDictField;
import com.web.common.dictionary.VerifyDictField.OperateMode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件目录
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-04
 */
@Getter
@Setter
@TableName("basic_file_catalog")
@Schema(title = "BasicFileCatalog对象", description = "文件目录")
public class BasicFileCatalog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目录的id */
    @Schema(title = "目录的id")
    @TableId(value = "catalog_id", type = IdType.AUTO)
    private Integer catalogId;
    
    /** 父级目录的id */
    @Schema(title = "父级目录的id")
    private Integer parentId;

    /** 目录地址 */
    @NotBlank(message = "请设置目录地址")
    @Schema(title = "目录地址")
    private String catalogName;
    
    /** 目录路径 */
    @Schema(title = "目录路径")
    private String catalogPath;

    /** 目录描述 */
    @Schema(title = "目录描述")
    private String catalogDescription;

    /** 目录权限，0读写权限；1只读权限 */
    @VerifyDictField(operateMode = OperateMode.VERIFY, dictType = "catalog_auth")
    @NotNull(message = "未设置目录权限")
    @Schema(title = "目录权限，0读写权限；1只读权限")
    private Integer authType;

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
