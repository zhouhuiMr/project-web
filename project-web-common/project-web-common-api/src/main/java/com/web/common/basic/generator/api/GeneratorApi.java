package com.web.common.basic.generator.api;

import java.util.ArrayList;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.generator.entity.GeneratorConfig;
import com.web.common.basic.generator.entity.GeneratorDatabase;
import com.web.common.basic.generator.entity.GeneratorTable;
import com.web.common.result.R;

/**
 * 代码生成。
 * 1、获取数据库列表；
 * 2、获取数据库对应的表名的列表；
 * 3、生成对应的文件。
 * 
 * @author zhouhui
 */
@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "generationApi", path = "/basic/generation")
public interface GeneratorApi {

	/**
	 * 获取数据库列表
	 * @return ResultJson<ArrayList<GenerationDatabase>> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(value = "/getDatabaseList")
	@ResponseBody
	R<ArrayList<GeneratorDatabase>> getDatabaseList();
	
	
	/**
	 * 根据数据库的名称获取对应的数据库表列表
	 * @param databaseName 数据库的名称
	 * @return R<ArrayList<GenerationTable>> 对应表的列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(value = "/getTableList")
	@ResponseBody
	R<ArrayList<GeneratorTable>> getTableList(GeneratorConfig config);
	
	/**
	 * 根据配置生成对应的代码信息
	 * @param config 配置信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(value = "/createGeneration")
	void createGeneration(GeneratorConfig config);
}
