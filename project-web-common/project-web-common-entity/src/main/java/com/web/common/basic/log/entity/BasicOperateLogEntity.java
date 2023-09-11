package com.web.common.basic.log.entity;

import java.time.LocalDate;
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
@Schema(title = "BasicOperateLogEntity对象", description = "操作日志记录")
public class BasicOperateLogEntity extends BasicOperateLog{

    private static final long serialVersionUID = 1L;
    
    /** 接口描述 */
	@Schema(title = "接口描述")
    private String apiDescribe;
    
    /** 接口调用次数 */
	@Schema(title = "接口调用次数")
    private Integer apiTimes;
	
	/** 开始时间 */
	@Schema(title = "开始时间")
	private LocalDate startDate;
	
	/** 结束时间 */
	@Schema(title = "结束时间")
	private LocalDate endDate;
	
	/** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
