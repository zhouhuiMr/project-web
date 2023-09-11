package com.web.basic.generator.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.web.common.basic.generator.entity.GeneratorConfig;
import com.web.common.basic.generator.entity.GeneratorDatabase;
import com.web.common.basic.generator.entity.GeneratorTable;
import com.web.common.file.FileToZip;
import com.web.common.file.FileTransition;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.Symbols;
import com.web.spring.tool.SpringBeanUtil;

@Service
public class GeneratorServiceImpl {
	
	@Value("${catalog.generator: ''}")
	private String filePath;
	
	private static final String FILE_ZIP_SUFFIX = ".zip";
	
	private static final String SEAR_DATA_TABLE = "SELECT TABLE_NAME,TABLE_COMMENT FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = ?";
	
	/**
	 * 获取系统配置的所有数据库列表
	 * @return R<ArrayList<GeneratorDatabase>> 数据库列表 
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public R<ArrayList<GeneratorDatabase>> getDatabaseList(){
		R<ArrayList<GeneratorDatabase>> json = new R<>();
		ApplicationContext context = SpringBeanUtil.getApplicationContext();
		Map<String, DruidDataSource> beanMap = context.getBeansOfType(DruidDataSource.class);
		ArrayList<GeneratorDatabase> databaseList = new ArrayList<>();
		
		Iterator<String> keys = beanMap.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			DruidDataSource dataSourceItem = beanMap.get(key);
			Connection conn = null;
			try {
				conn = dataSourceItem.getConnection();
				if(conn == null) {
					continue;
				}
				GeneratorDatabase database = new GeneratorDatabase();
				database.setDatabaseName(conn.getCatalog());
				databaseList.add(database);
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				if(conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(databaseList);
		return json;
	}
	
	/**
	 * 获取数据库的所有表名称
	 * @param databaseName 数据库名称
	 * @return  R<ArrayList<GeneratorTable>> 数据库表的列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public R<ArrayList<GeneratorTable>> getTableList(GeneratorConfig config){
		R<ArrayList<GeneratorTable>> json = new R<>();
		DruidDataSource dataSource = getConnectConfig(config);
		if(dataSource == null) {
			json.setResultEnum(ResultEnum.ERROR);
			return json;
		}
		String sql = SEAR_DATA_TABLE;
		if(StringUtils.hasText(config.getTableName())) {
			sql += " AND TABLE_NAME LIKE ?";
			config.setTableName("%" + config.getTableName() + "%");
		}
		
		ArrayList<GeneratorTable> tableList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try {
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(sql);
			statement.setString(1, config.getDatabaseName());
			if(StringUtils.hasText(config.getTableName())) {
				statement.setString(2, config.getTableName());
			}
			result = statement.executeQuery();
			if(result == null) {
				json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
				return json;
			}
			while(result.next()) {
				String tableName = result.getString("TABLE_NAME");
				String tableDescription = result.getString("TABLE_COMMENT");
				
				GeneratorTable table = new GeneratorTable();
				table.setTableName(tableName);
				table.setTableDescription(tableDescription);
				tableList.add(table);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(statement != null){
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(tableList);
		return json;
	}
	
	/**
	 * 根据数据库表生成对应的代码
	 * @param config 配置信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public R<byte[]> createGeneration(GeneratorConfig config){
		R<byte[]> json = new R<>();
		String rootPath = filePath;
		if(!StringUtils.hasText(rootPath)) {
			rootPath = System.getProperty("projectpath");
		}
		
		DruidDataSource dataSource = getConnectConfig(config);
		if(dataSource == null) {
			json.setResultEnum(ResultEnum.ERROR);
			return json;
		}
		FastAutoGenerator generator = FastAutoGenerator.create(dataSource.getRawJdbcUrl(), dataSource.getUsername(), dataSource.getPassword());
		String outPath = rootPath;
		generator.globalConfig(builder -> {
			builder.author(config.getAuthor());
			builder.enableSwagger();
			builder.commentDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			builder.fileOverride();
			builder.outputDir(outPath);
		});
		generator.packageConfig(builder -> {
			builder.parent(config.getPackageName());
			builder.moduleName(config.getModelName());
		});
		generator.strategyConfig(build -> {
			build.addInclude(config.getTableName());
			if(StringUtils.hasText(config.getTablePrefix())) {
				build.addTablePrefix(config.getTablePrefix());
			}
			//entity
			build.entityBuilder()
			.enableLombok();
			//service
			build.serviceBuilder()
			.formatServiceFileName("%sService");
			//mapper
			build.mapperBuilder()
			.enableBaseResultMap()
			.enableBaseColumnList();
		});
		
		generator.templateEngine(new FreemarkerTemplateEngine());
		generator.templateConfig(build -> {
			build.entity("/template/entity.java");
			build.controller("/template/controller.java");
			build.service("/template/service.java");
			build.serviceImpl("/template/serviceImpl.java");
			build.mapper("/template/mapper.java");
			build.mapperXml("/template/mapper.xml");
		});
		
		generator.injectionConfig(consumer -> 
			consumer.beforeOutputFile((tableInfo, objectMap) -> {
				Map<String, String> customFile = new HashMap<>();
                customFile.put(tableInfo.getEntityName() + "Entity.java", "/template/entity.subclass.java.ftl");
                consumer.customFile(customFile);
		    })
		);
		generator.execute();
		
		//将文件写入zip
		fileToZip(rootPath, config);
		//将文件转成byte[]
		String zipPath = rootPath + Symbols.FORWARD_SLASH + config.getModelName() + FILE_ZIP_SUFFIX;
		byte[] outByte = FileTransition.fileToByte(zipPath,true);
		if(outByte.length == 0) {
			json.setResultEnum(ResultEnum.FILE_TRANSITION_ZIP_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(outByte);
		return json;
	}
	
	/**
	 * 获取数据库配置的。
	 * @param config 生成代码的配置信息
	 * @return DruidDataSource 对应的数据源
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private DruidDataSource getConnectConfig(GeneratorConfig config) {
		if(!StringUtils.hasText(config.getDatabaseName())) {
			return null;
		}
		ApplicationContext context = SpringBeanUtil.getApplicationContext();
		Map<String, DruidDataSource> beanMap = context.getBeansOfType(DruidDataSource.class);
		Iterator<String> keys = beanMap.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			DruidDataSource dataSourceItem = beanMap.get(key);
			
			Connection conn = null;
			try {
				conn = dataSourceItem.getConnection();
				if(conn == null) {
					continue;
				}
				if(config.getDatabaseName().equals(conn.getCatalog())) {
					return dataSourceItem;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将文件转成zip文件
	 * @param rootPath 文件根目录
	 * @param config 配置信息
	 * @return boolean true压缩成功；false压缩失败
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private boolean fileToZip(String rootPath, GeneratorConfig config) {
		if(!StringUtils.hasText(config.getPackageName())) {
			return false;
		}
		//获取生成代码的文件目录结构
		String[] splitePath = config.getPackageName().split("\\.");
		if(splitePath == null || splitePath.length <= 0) {
			return false;
		}
		String source = rootPath + "/" + splitePath[0];
		String target = rootPath + "/" + config.getModelName() + FILE_ZIP_SUFFIX;
		
		FileToZip.fileToZip(target, source, true);
		
		return true;
	}
}
