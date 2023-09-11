package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(title = "数据大屏的查询条件", description = "数据大屏的查询条件")
public class DataBoardSearchParam {
	
	/** 统计接口调用次数时间间隔（单位：分钟） */
	@Schema(title = "统计接口调用次数时间间隔（单位：分钟）")
	private Integer apiTimesInterval;
	
	/** 统计数据次数 */
	@Schema(title = "统计数据次数")
	private Integer apiTimesCount;
	
	/** 获取接口调用次数最多的行数 */
	@Schema(title = "获取接口调用次数最多的行数")
	private Integer topApiRows;
}
