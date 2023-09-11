package com.web.common.basic.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "elasticsearchApi", path = "/basic/elasticsearch")
public interface ElasticsearchApi {

	/**
	 * 获取索引列表
	 * @return R<List<String>> 索引列表
	 *
	 * @author zhouhui
	 * @since 2023.07.01
	 */
	@PostMapping(path = "/index/list")
	@ResponseBody
	R<List<String>> getIndexList();
	
	/**
	 * 创建索引
	 * @param index 索引值
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.07.01
	 */
	@PostMapping(path = "/createIndex")
	@ResponseBody
	R<Object> createIndex(@RequestParam("index") String index);
}
