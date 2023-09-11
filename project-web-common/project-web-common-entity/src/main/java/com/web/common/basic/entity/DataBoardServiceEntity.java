package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "系统服务信息看板对象", description = "系统服务信息看板对象")
public class DataBoardServiceEntity {
	
	/** 服务名称 */
	@Schema(title = "服务名称")
	private String serviceName;
	
	/** 服务调用次数 */
	@Schema(title = "服务调用次数")
	private String apiTimes;
}
