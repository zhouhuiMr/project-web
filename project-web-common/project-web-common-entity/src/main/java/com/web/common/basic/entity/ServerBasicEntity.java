package com.web.common.basic.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "服务器信息对象", description = "服务器信息对象")
public class ServerBasicEntity {

	/** 最小值 */
	@JsonProperty("Minimum")
    @Schema(title = "最小值")
	private String minimum;
    
    /** 最大值 */
	@JsonProperty("Maximum")
    @Schema(title = "最大值")
    private String maximum;
    
    /** 平均值 */
	@JsonProperty("Average")
    @Schema(title = "平均值")
    private String average;
	
	/** 时间戳 */
    @Schema(title = "时间戳")
	private Long timestamp;
    
    /** 时间戳 */
    @Schema(title = "时间戳")
    private LocalDateTime showTime;
}
