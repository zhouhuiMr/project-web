package com.web.common.user.entity;

import java.util.List;

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
@Schema(title = "WebDeptEntity对象", description = "部门信息")
public class WebDeptEntity extends WebDept{

    private static final long serialVersionUID = 1L;
    
    /** 子部门列表 */
    @Schema(title = "子部门列表")
    private List<WebDeptEntity> children;
}
