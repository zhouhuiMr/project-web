package com.web.common.basic.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "服务器云盘或本地盘信息对象", description = "服务器云盘或本地盘信息对象")
public class ServerDiskEntity {

	/** 设备名称 */
	@Schema(title = "设备名称")
	private String device;
	
	/** 盘符名称 */
	@Schema(title = "盘符名称")
	private String diskname;
	
	/** 云盘或本地盘大小，单位GiB */
	@Schema(title = "云盘或本地盘大小，单位GiB")
	private Long size;
	
	/** 云盘或本地盘已使用的大小，单位GiB */
	@Schema(title = "云盘或本地盘已使用的大小，单位GiB")
	private Long useSize;
	
	/** 云盘或本地盘使用率（%） */
	@Schema(title = "云盘或本地盘使用率（%）")
	private Long usageRate;
    
    /** 平均大小（byte） */
	@JsonProperty("Average")
    @Schema(title = "平均大小（byte）")
    private Long average;
}
