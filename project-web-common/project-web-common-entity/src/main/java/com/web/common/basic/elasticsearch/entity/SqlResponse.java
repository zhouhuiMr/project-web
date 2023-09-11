package com.web.common.basic.elasticsearch.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqlResponse {

	/**  */
	private String id;
	
	/** 是否还在执行，true还在执行；false执行完成 */
	@JsonProperty(value = "is_running")
	private Boolean isRunning;
	
	/** 是否不完整，true不完整；false完整 */
	@JsonProperty(value = "is_partial")
	private Boolean isPartial;
	
	/** 字段列 */
	private List<ColumnType> columns;
	
	/** 数据行 */
	private List<List<String>> rows;
	
	/** 游标 */
	private String cursor;
}
