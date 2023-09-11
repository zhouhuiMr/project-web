package com.web.common.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "服务器配置信息对象", description = "服务器配置信息对象")
public class ServerConfigEntiy {

	/** 服务器名称 */
    @Schema(title = "服务器名称")
	private String name;
	
    /** 服务器IP */
    @JsonIgnore
    @Schema(title = "服务器IP")
	private String ip;
	
    /** 服务器实例名称 */
    @Schema(title = "服务器实例名称")
	private String instanceId;
    
    /** 服务器所在地区 */
    @Schema(title = "服务器所在地区")
    private String region;
}
