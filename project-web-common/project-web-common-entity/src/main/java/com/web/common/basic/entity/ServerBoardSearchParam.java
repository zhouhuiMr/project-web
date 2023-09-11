package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(title = "服务器信息查询条件", description = "服务器信息查询条件")
public class ServerBoardSearchParam {

	/** 服务器实例名称 */
    @Schema(title = "服务器实例名称")
	private String instanceId;
    
    /** 服务器所在地区 */
    @Schema(title = "服务器所在地区")
    private String region;
    
    /** 监控项名称 */
    @Schema(title = "监控项名称")
    private String metricName;
}
