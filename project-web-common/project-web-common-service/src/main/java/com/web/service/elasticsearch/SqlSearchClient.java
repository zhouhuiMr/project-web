package com.web.service.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.basic.elasticsearch.entity.ColumnType;
import com.web.common.basic.elasticsearch.entity.SqlResponse;
import com.web.service.handler.error.CustomException;

@Component
public class SqlSearchClient {

	@Autowired
	private RestClient elRestClient;

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * 执行SQL语句获取结果
	 * @param param 查询数据
	 * @return SqlResponse 查询结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public SqlResponse executeSql(String param) {
		Request request = new Request(HttpMethod.POST.name(), "_sql?format=json");
		request.setJsonEntity(param);
		Response response = null;
		try {
			response = elRestClient.performRequest(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response == null) {
			throw new CustomException("接口调用失败");
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (HttpStatus.OK.value() != statusCode) {
			throw new CustomException("接口调用失败，状态码：" + statusCode);
		}
		SqlResponse sqlResult = null;
		try {
			String body = EntityUtils.toString(response.getEntity());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			sqlResult = mapper.readValue(body, SqlResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sqlResult == null) {
			throw new CustomException("解析返回结果失败");
		}
		return sqlResult;
	}
	
	/**
	 * 执行SQL语句获取结果
	 * @param param 查询数据
	 * @return SqlResponse 查询结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public SqlResponse executeSql(Map<String, String> param) {
		return executeSql(convertMapToJson(param));
	}
	
	/**
	 * 将获取结果转成map数据
	 * @param param 执行的参数
	 * @return List<Map<String, String>> json结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public List<Map<String, String>> executeSqlToMap(String param){
		SqlResponse sqlResult = executeSql(param);
		if (sqlResult == null) {
			return Collections.emptyList();
		}
		List<ColumnType> columns = sqlResult.getColumns();
		List<List<String>> rows = sqlResult.getRows();
		return dataConvertMap(rows, columns);
	}
	
	/**
	 * 将获取结果转成map数据
	 * @param param 执行的参数
	 * @return List<Map<String, String>> json结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public List<Map<String, String>> executeSqlToMap(Map<String, String> param){
		return executeSqlToMap(convertMapToJson(param));
	}

	/**
	 * 将获取结果转成json数据
	 * @param param 执行的参数
	 * @return String json结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public String executeSqlToJson(String param) {
		List<Map<String, String>> mapList = executeSqlToMap(param);
		String dataJson = null;
		try {
			dataJson = mapper.writeValueAsString(mapList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return dataJson;
	}
	
	/**
	 * 将获取结果转成json数据
	 * @param param 执行的参数
	 * @return String json结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public String executeSqlToJson(Map<String, String> param) {
		return executeSqlToJson(convertMapToJson(param));
	}

	/**
	 * 将数据转成Map列表
	 * @param data 数据列表
	 * @param columns 列的名称/类型列表
	 * @return List<Map<String, String>> Map列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private List<Map<String, String>> dataConvertMap(List<List<String>> data, List<ColumnType> columns) {
		List<Map<String, String>> mapList = new ArrayList<>(data.size());
		for (List<String> item : data) {
			Map<String, String> mapItem = new HashedMap<>();
			for (int i = 0; i < columns.size(); i++) {
				ColumnType column = columns.get(i);
				String fieldName = column.getName();
				if(i < item.size()) {
					mapItem.put(fieldName, item.get(i));
				}
			}
			mapList.add(mapItem);
		}
		return mapList;
	}
	
	/**
	 * 将Map转成JSON字符串
	 * @param param 请求参数
	 * @return String JSON字符串
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private String convertMapToJson(Map<String, String> param) {
		String requestParam = null;
		try {
			requestParam = mapper.writeValueAsString(param);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(requestParam == null) {
			throw new CustomException("请求参数解析异常");
		}
		return requestParam;
	}
	
	/**
	 * 对象与JSON转换的工具
	 * @return ObjectMapper
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public ObjectMapper getObjectMapper() {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
}
