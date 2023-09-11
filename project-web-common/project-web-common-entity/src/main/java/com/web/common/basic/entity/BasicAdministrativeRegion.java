package com.web.common.basic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 行政区划 省、市、区和街道
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-22
 */
@Getter
@Setter
@TableName("basic_administrative_region")
@Schema(title = "BasicAdministrativeRegion对象", description = "行政区划 省、市、区和街道")
public class BasicAdministrativeRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "main_id", type = IdType.AUTO)
    private Integer mainId;

    /** 省 */
    @Schema(title = "省")
    private String provName;

    /** 省的编号 */
    @Schema(title = "省的编号")
    private String provCode;

    /** 市 */
    @Schema(title = "市")
    private String cityName;

    /** 市的编号 */
    @Schema(title = "市的编号")
    private String cityCode;

    /** 区 */
    @Schema(title = "区")
    private String counName;

    /** 区的编号 */
    @Schema(title = "区的编号")
    private String counCode;

    /** 街道 */
    @Schema(title = "街道")
    private String townName;

    /** 街道编号 */
    @Schema(title = "街道编号")
    private String townCode;


}
