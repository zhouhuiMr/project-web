package com.web.common.basic.generator.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 数据库
 * 
 * @author zhouhui
 */
@Data
public class GeneratorDatabase {

	/** 数据库名称 */
	private String databaseName;
	
	/** 数据库描述 */
	private String databaseDescription;
	
	/** 数据库对应表的列表 */
	private List<GeneratorTable> tableList = new ArrayList<>();
}
