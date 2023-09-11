package com.web.common.basic.log.entity;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 操作日志记录
 * </p>
 *
 * @author zhouhui
 * @since 2023-01-08
 */
@Getter
@Setter
@Schema(title = "BasicOperateLog对象", description = "操作日志记录")
public class ProjectWebLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 调用的服务名称 */
    @NotNull(message = "未设置服务名称")
    @Schema(title = "调用的服务名称")
    private String serviceName;

    /** 请求类型 */
    @Schema(title = "请求类型")
    private String contentType;

    /** 请求方式，GET、POST等 */
    @Schema(title = "请求方式，GET、POST等")
    private String requestMethod;

    /** 请求的路径 */
    @NotNull(message = "未设置请求路径")
    @Schema(title = "请求的路径")
    private String path;
    
    /** 操作用户ID */
    @Schema(title = "操作用户ID")
    private Integer userId;

    /** 操作用户code */
    @Schema(title = "操作用户code")
    private String userCode;

    /** 操作用户的nickname */
    @Schema(title = "操作用户的nickname")
    private String userNickname;
    
    /** 请求的路径 */
    @Schema(title = "请求的路径")
    private Integer httpStatus;
    
    /** 返回结果的状态码 */
    @Schema(title = "返回结果的状态码")
    private String resultCode;
    
    /** 返回结果的信息 */
    @Schema(title = "返回结果的信息")
    private String resultMessage;

    /** 创建年份 yyyy */
    @Schema(title = "创建年份 yyyy")
    private Integer createYear;

    /** 创建时间，yyyy-MM-dd */
    @Schema(title = "创建时间，yyyy-MM-dd")
    private String createDate;

    /** 创建的时间 */
    @Schema(title = "创建的时间")
    private String createTime;
}
