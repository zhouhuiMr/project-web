package com.web.basic.service;

import java.util.List;
import com.web.common.result.R;

public interface ElasticsearchService {

	/**
	 * 获取所有的索引
	 * @return R<List<String>> 索引列表
	 *
	 * @author zhouhui
	 * @since 2023.07.01
	 */
	R<List<String>> getIndexList();
	
	/**
	 * 创建索引
	 * @param index 索引名称
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.07.01
	 */
	R<Object> createIndex(String index);
	
	
	/**
	 * 定期删除日志信息
	 * @return R<Object>处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.16 
	 */
	R<Object> periodicDeleteLog();
}
