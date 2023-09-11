package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "数据比较对象", description = "数据比较对象")
public class DataBoardComparisonEntity {

	/** 当前数据 */
	@Schema(title = "当前数据")
	private String curData;
	
	/** 历史数据 */
	@Schema(title = "历史数据")
	private String historicalData;
	
	/** 差值 */
	@Schema(title = "差值")
	private String differenceValue;
	
	/** 变化方式，0不变；1增加；2减少 */
	@Schema(title = "变化方式，0不变；1增加；2减少")
	private Integer changeStatus;
}
