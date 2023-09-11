package com.web.basic.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.web.basic.service.ElasticsearchService;
import com.web.common.basic.entity.ElasticsearchIndex;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;
import com.web.config.init.ElasticsearchMapping;
import com.web.service.handler.error.CustomException;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

	@Autowired
	private ElasticsearchMapping elasticsearchMapping;

	@Autowired
	private RestClient elRestClient;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	@Override
	public R<List<String>> getIndexList() {
		R<List<String>> json = new R<>();
		Map<String, String> dataMap = elasticsearchMapping.getJsonIndex();
		if (dataMap.isEmpty()) {
			json.setData(new ArrayList<>());
			json.setResultEnum(ResultEnum.SUCCESS);
			return json;
		}
		List<String> indexList = new ArrayList<>(dataMap.size());
		Iterator<String> indexIterator = dataMap.keySet().iterator();
		while (indexIterator.hasNext()) {
			indexList.add(indexIterator.next());
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(indexList);
		return json;
	}

	@Override
	public R<Object> createIndex(String index) {
		R<Object> json = new R<>();
		if (!StringUtils.hasText(index)) {
			throw new CustomException("请选择索引");
		}
		Map<String, String> indexMap = elasticsearchMapping.getJsonIndex();
		if (indexMap.get(index) == null) {
			throw new CustomException("未查询到对应的索引值");
		}
		Request request = new Request("PUT", "/" + index);
		Response res = null;
		try {
			request.setJsonEntity(indexMap.get(index));
			res = elRestClient.performRequest(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (res == null) {
			throw new CustomException("创建索引请求API失败");
		}
		int statusCode = res.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.OK.value()) {
			throw new CustomException("创建索引请失败");
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> periodicDeleteLog() {
		R<Object> json = new R<>();
		//定期删除两年前的数据
		try {
			LocalDate curDate = LocalDate.now();
			String deleteDate = curDate.minusYears(2).format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE));
			Query query = Query.of(q -> q.match(t -> t.field("createDate").query(deleteDate)));
			elasticsearchClient.deleteByQuery(fn -> fn.index(ElasticsearchIndex.LOG_INDEX).query(query)).deleted();
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
}
