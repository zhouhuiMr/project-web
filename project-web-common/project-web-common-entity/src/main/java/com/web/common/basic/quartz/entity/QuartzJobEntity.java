package com.web.common.basic.quartz.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 定时任务名称
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Getter
@Setter
@Schema(title = "QuartzJobEntity对象", description = "定时任务名称")
public class QuartzJobEntity extends QuartzJob{

    private static final long serialVersionUID = 1L;
    
    /** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
