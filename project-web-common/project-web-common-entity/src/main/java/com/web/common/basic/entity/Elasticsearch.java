package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "elasticsearch对象", description = "elasticsearch对象")
public class Elasticsearch {

	/** 索引 */
	@Schema(title = "索引")
	private String index;
	
	/** 命令值 */
	@Schema(title = "命令值")
	private String cmd;
	
	/** 执行参数 */
	@Schema(title = "执行参数")
	private String param;
}
