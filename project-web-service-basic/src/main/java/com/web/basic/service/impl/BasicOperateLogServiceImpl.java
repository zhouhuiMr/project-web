package com.web.basic.service.impl;

import com.web.basic.mapper.BasicOperateLogMapper;
import com.web.common.basic.entity.ElasticsearchIndex;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.web.common.basic.log.entity.BasicOperateLogEntity;
import com.web.common.basic.log.entity.ProjectWebLog;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;
import com.web.common.tool.Symbols;
import com.web.service.handler.error.CustomException;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-01-08
 */
@Service
public class BasicOperateLogServiceImpl extends ServiceImpl<BasicOperateLogMapper, BasicOperateLog> {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private RestClient elRestClient;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	/**
	 * 保存日志信息
	 * @param operateLog 日志信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.19
	 */
	public R<Object> saveOperateLog(BasicOperateLog operateLog) {
		R<Object> json = new R<>();

		LocalDateTime dateTime = LocalDateTime.now();
		LocalDate date = LocalDate.now();
		operateLog.setCreateTime(dateTime);
		operateLog.setCreateDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
		operateLog.setCreateYear(Integer.valueOf(date.format(DateTimeFormatter.ofPattern("yyyy"))));

		try {
			JavaTimeModule module = new JavaTimeModule();
			module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
			mapper.registerModule(module);

			String body = mapper.writeValueAsString(operateLog);

			UUID uuid = UUID.randomUUID();
			Request request = new Request("POST",
					Symbols.FORWARD_SLASH + ElasticsearchIndex.LOG_INDEX + "/_doc/" + uuid.toString());
			request.setJsonEntity(body);
			elRestClient.performRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 获取操作日志
	 * @param condition 查询条件
	 * @return R<IPage<BasicOperateLogEntity>> 分页列表
	 *
	 * @author zhouhui
	 * @since 2023.08.19
	 */
	public R<IPage<ProjectWebLog>> getOperateLogList(BasicOperateLogEntity condition) {
		R<IPage<ProjectWebLog>> json = new R<>();
		setSearchCondition(condition);
		
		SearchResponse<ProjectWebLog> result = null;
		try {
			List<Query> queryList = setLogQueryList(condition);
			SortOptions sort = SortOptions.of(fn -> fn.field(f -> f.field("createTime").order(SortOrder.Desc)));	
			
			SearchRequest request = SearchRequest
					.of(fn -> fn.index(ElasticsearchIndex.LOG_INDEX)
					.query(q -> q.bool(b -> b.must(queryList)))
					.sort(sort)
					.from((condition.getPage() - 1) * condition.getSize())
					.size(condition.getSize()));
			result = elasticsearchClient.search(request, ProjectWebLog.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		
		List<Hit<ProjectWebLog>> hits = result.hits().hits();
		List<ProjectWebLog> logList = new ArrayList<>(hits.size());
		for(Hit<ProjectWebLog> item: hits) {
			ProjectWebLog source = item.source();
			if(StringUtils.hasText(source.getCreateTime())) {
				LocalDateTime createTime = LocalDateTime.parse(source.getCreateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				source.setCreateTime(createTime.format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE_TIME)));
			}
			logList.add(item.source());
		}
		IPage<ProjectWebLog> page = new Page<>(condition.getPage(), condition.getSize());
		page.setTotal(result.hits().total().value());
		page.setRecords(logList);
		json.setData(page);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	/**
	 * 设置查询条件
	 * @param condition 查询信息
	 *
	 * @author zhouhui
	 * @since 2023.08.19
	 */
	private void setSearchCondition(BasicOperateLogEntity condition) {
		if(condition.getStartDate() == null || condition.getEndDate() == null) {
			throw new CustomException("未选择时间范围");
		}
		if(condition.getEndDate().isBefore(condition.getStartDate())) {
			throw new CustomException("开始时间不能大于结束时间");
		}
		int year = condition.getStartDate().getYear() - condition.getEndDate().getYear();
		if(year > 1) {
			throw new CustomException("查询数据范围不能超过一年");
		}
	}
	
	/**
	 * 设置日志的查询条件列表
	 * @param condition 查询条件
	 * @return List<Query> 查询条件列表
	 *
	 * @author zhouhui
	 * @since 2023.08.19
	 */
	private List<Query> setLogQueryList(BasicOperateLogEntity condition) {
		String startDate = condition.getStartDate().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE));
		String endDate = condition.getEndDate().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE));
		
		List<Query> queryList = new ArrayList<>();
		Query dateQuery = Query.of(q -> q.range(range -> range.field("createDate").gte(JsonData.of(startDate)).lte(JsonData.of(endDate))));
		queryList.add(dateQuery);
		if(StringUtils.hasText(condition.getPath())) {
			Query pathQuery = Query.of(q -> q.match(m -> m.field("path").query(condition.getPath()).operator(Operator.And)));
			queryList.add(pathQuery);
		}
		if(StringUtils.hasText(condition.getUserCode())) {
			Query userCodeQuery = Query.of(q -> q.match(m -> m.field("userCode").query(condition.getUserCode())));
			queryList.add(userCodeQuery);
		}
		if(StringUtils.hasText(condition.getResultCode())) {
			Query resultCodeQuery = Query.of(q -> q.match(m -> m.field("resultCode").query(condition.getResultCode())));
			queryList.add(resultCodeQuery);
		}
		return queryList;
	}
}
