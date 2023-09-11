package com.web.basic.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.web.basic.service.ElasticsearchService;
import com.web.common.basic.api.ElasticsearchApi;
import com.web.common.result.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "elasticsearch功能")
@Controller
@RequestMapping(path = "/basic/elasticsearch")
public class ElasticsearchController implements ElasticsearchApi{

	@Autowired
	private ElasticsearchService elasticsearchServiceImpl;
	
	@Operation(summary = "获取所有索引")
	@Override
	public R<List<String>> getIndexList() {
		return elasticsearchServiceImpl.getIndexList();
	}

	@Operation(summary = "创建索引")
	@Override
	public R<Object> createIndex(String index) {
		return elasticsearchServiceImpl.createIndex(index);
	}

}
