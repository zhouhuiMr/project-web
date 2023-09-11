package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "接口调用信息看板对象", description = "接口调用信息看板对象")
public class DataBoardApiEntity {

	/** 统计接口调用的开始时间 */
	@Schema(title = "统计接口调用的开始时间")
	private String apiStartDateTime;
	
	/** 统计接口调用的结束时间 */
	@Schema(title = "统计接口调用的结束时间")
	private String apiEndDateTime;
	
	/** 接口调用的次数 */
	@Schema(title = "接口调用的次数")
	private Integer apiTimes;
	
	/** 接口调用成功的次数 */
	@Schema(title = "接口调用成功的次数")
	private Integer apiSuccessTimes;
	
	/** 接口调用失败的次数 */
	@Schema(title = "接口调用失败的次数")
	private Integer apiFailedTimes;
}
