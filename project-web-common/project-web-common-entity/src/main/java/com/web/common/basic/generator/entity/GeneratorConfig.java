package com.web.common.basic.generator.entity;

import lombok.Data;

/**
 * 生成代码的配置信息
 * 
 * @author zhouhui
 */
@Data
public class GeneratorConfig {
	
	/** 数据库名称 */
	private String databaseName;

	/** 作者 */
	private String author;
	
	/** 包名 */
	private String packageName;
	
	/** 模块名 */
	private String modelName;
	
	/** 输出的文件路径 */
	private String outputDir;
	
	/** 对应的表名 */
	private String tableName;
	
	/** 需要过滤的表前缀 */
	private String tablePrefix;
}
