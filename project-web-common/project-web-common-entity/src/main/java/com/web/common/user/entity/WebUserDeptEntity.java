package com.web.common.user.entity;

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
@Schema(title = "WebUserDeptEntity对象", description = "用户对应的部门信息")
public class WebUserDeptEntity extends WebUserDept{

    private static final long serialVersionUID = 1L;
    
    /** 部门名称 */
    @Schema(title = "部门名称")
    private String deptName;
}
