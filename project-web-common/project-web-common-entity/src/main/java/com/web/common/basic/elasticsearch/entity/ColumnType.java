package com.web.common.basic.elasticsearch.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnType {

	/** 列字段名称 */
	private String name;
	
	/** 列字段类型 */
	private String type;
}
