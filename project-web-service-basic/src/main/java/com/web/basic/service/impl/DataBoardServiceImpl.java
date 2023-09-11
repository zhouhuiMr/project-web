package com.web.basic.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.aliyun.cms20190101.Client;
import com.aliyun.cms20190101.models.DescribeMetricLastRequest;
import com.aliyun.cms20190101.models.DescribeMetricLastResponse;
import com.aliyun.cms20190101.models.DescribeMetricListRequest;
import com.aliyun.cms20190101.models.DescribeMetricListResponse;
import com.aliyun.sdk.service.ecs20140526.AsyncClient;
import com.aliyun.sdk.service.ecs20140526.models.DescribeInstanceAttributeRequest;
import com.aliyun.sdk.service.ecs20140526.models.DescribeInstanceAttributeResponse;
import com.aliyun.sdk.service.ecs20140526.models.DescribeInstanceAttributeResponseBody;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.basic.mapper.BasicOperateLogMapper;
import com.web.basic.service.DataBoardService;
import com.web.common.basic.entity.DataBoardApiEntity;
import com.web.common.basic.entity.DataBoardComparisonEntity;
import com.web.common.basic.entity.DataBoardEntity;
import com.web.common.basic.entity.DataBoardSearchParam;
import com.web.common.basic.entity.DataBoardServiceEntity;
import com.web.common.basic.entity.DateTimeRange;
import com.web.common.basic.entity.ElasticsearchIndex;
import com.web.common.basic.entity.ServerBasicEntity;
import com.web.common.basic.entity.ServerBoardEntity;
import com.web.common.basic.entity.ServerBoardSearchParam;
import com.web.common.basic.entity.ServerCpuEntity;
import com.web.common.basic.entity.ServerDiskEntity;
import com.web.common.basic.entity.ServerMemoryEntity;
import com.web.common.properties.SecurityLoginEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;
import com.web.service.elasticsearch.SqlSearchClient;
import com.web.service.handler.error.CustomException;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.json.JsonData;

@Service
public class DataBoardServiceImpl implements DataBoardService {

	/** elasticsearch查询参数 */
	private static final String QUERY_PARAM_NAME = "query";

	/** 查询语句 */
	private static final String SQL_TIMES = "SELECT COUNT(serviceName) apiTimes, DATE_FORMAT(createDate, '%Y-%m-%d') createDate FROM "
			+ ElasticsearchIndex.LOG_INDEX + " ";

	/** 分组信息 */
	private static final String SQL_TIMES_GROUP = "GROUP BY createDate ORDER BY createDate DESC";

	@Autowired
	private SecurityLoginEntity securityLoginEntity;

	@Autowired
	private SqlSearchClient sqlSearchClient;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	@Autowired
	private BasicOperateLogMapper basicOperateLogMapper;

	@Autowired
	private Client cloudMonitorClient;

	@Autowired
	private AsyncClient escClient;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public R<DataBoardEntity> showDataBoard(DataBoardSearchParam condition) {
		R<DataBoardEntity> json = new R<>();
		DataBoardEntity result = new DataBoardEntity();

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE));
		String yesterday = LocalDate.now().minusDays(1)
				.format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE));

		// 注册数量（当天/前一天）
		statisticsRegisterUser(result, today, yesterday);
		// 登录次数（当天/前一天）
		statisticsLoginTimes(result, today, yesterday);
		// 接口调用总次数（当天/前一天）
		statisticsApiTimes(result, today, yesterday);
		// 接口调用失败次数（当天/前一天）
		statisticsApiErrorTimes(result, today, yesterday);

		// 按照时间段分段内接口调用次数
		getApiTimesByTimeGroup(condition, result);
		// 按照服务类型统计接口调用次数
		getApiTimesByService(today, result);

		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(result);
		return json;
	}

	/**
	 * 获取注册用户数量
	 * 
	 * @param result    返回的数据结果
	 * @param today     今天日期
	 * @param yesterday 前一天日期
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void statisticsRegisterUser(DataBoardEntity result, String today, String yesterday) {
		today = today + " 23:59:59";
		yesterday = yesterday + " 23:59:59";
		DataBoardComparisonEntity comparison = basicOperateLogMapper.statisticsRegisterUser(today, yesterday);
		if (comparison == null) {
			return;
		}
		if (StringUtils.hasText(comparison.getDifferenceValue())) {
			int changeValue = Integer.parseInt(comparison.getDifferenceValue());
			if (changeValue > 0) {
				comparison.setChangeStatus(1);
			} else if (changeValue < 0) {
				comparison.setChangeStatus(2);
			} else {
				comparison.setChangeStatus(0);
			}
		}
		result.setRegisterUserCount(comparison);
	}

	/**
	 * 统计今日的登录次数
	 * 
	 * @param result    返回的数据结果
	 * @param today     今天日期
	 * @param yesterday 前一天日期
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void statisticsLoginTimes(DataBoardEntity result, String today, String yesterday) {
		Map<String, String> searchCondition = new HashedMap<>();

		String sql = "";
		sql += SQL_TIMES;
		sql += "WHERE resultCode = '0000' AND httpStatus = 200 AND createDate IN ('" + today + "', '" + yesterday
				+ "') ";
		sql += "AND path='" + securityLoginEntity.getProcessingUrl() + "' ";
		sql += SQL_TIMES_GROUP;
		searchCondition.put(QUERY_PARAM_NAME, sql);

		List<Map<String, String>> dataList = sqlSearchClient.executeSqlToMap(searchCondition);
		if (dataList == null) {
			return;
		}
		result.setUserLoginCount(calculateApiTimes(dataList, today, yesterday));
	}

	/**
	 * 统计当天与前一天接口调用总次数
	 * 
	 * @param result    返回的数据结果
	 * @param today     今天日期
	 * @param yesterday 前一天日期
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void statisticsApiTimes(DataBoardEntity result, String today, String yesterday) {
		Map<String, String> searchCondition = new HashedMap<>();

		String sql = "";
		sql += SQL_TIMES;
		sql += "WHERE createDate IN ('" + today + "', '" + yesterday + "') ";
		sql += SQL_TIMES_GROUP;
		searchCondition.put(QUERY_PARAM_NAME, sql);

		List<Map<String, String>> dataList = sqlSearchClient.executeSqlToMap(searchCondition);
		if (dataList == null) {
			return;
		}
		result.setApiTimes(calculateApiTimes(dataList, today, yesterday));
	}

	/**
	 * 统计当天与前一天接口调用错误次数
	 * 
	 * @param result    处理结果
	 * @param today     今天日期
	 * @param yesterday 前一天的日期
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void statisticsApiErrorTimes(DataBoardEntity result, String today, String yesterday) {
		Map<String, String> searchCondition = new HashedMap<>();

		String sql = "";
		sql += SQL_TIMES;
		sql += "WHERE createDate IN ('" + today + "', '" + yesterday
				+ "') AND (httpStatus <> 200 OR resultCode <> '0000') ";
		sql += SQL_TIMES_GROUP;
		searchCondition.put(QUERY_PARAM_NAME, sql);

		List<Map<String, String>> dataList = sqlSearchClient.executeSqlToMap(searchCondition);
		if (dataList == null) {
			return;
		}
		result.setApiErrorTimes(calculateApiTimes(dataList, today, yesterday));
	}

	/**
	 * 计算接口调用次数
	 * 
	 * @param dataList  数据列表
	 * @param today     今天日期
	 * @param yesterday 前一天的日期
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private DataBoardComparisonEntity calculateApiTimes(List<Map<String, String>> dataList, String today,
			String yesterday) {
		Map<String, String> dataMap = new HashedMap<>();
		for (Map<String, String> item : dataList) {
			String createDate = item.get("createDate");
			String apiTimes = item.get("apiTimes");
			dataMap.put(createDate, apiTimes);
		}
		int todayApiTimes = getApiTimes(dataMap, today);
		int yesterdayApiTimes = getApiTimes(dataMap, yesterday);

		DataBoardComparisonEntity comparison = new DataBoardComparisonEntity();
		comparison.setCurData(todayApiTimes + "");
		comparison.setHistoricalData(yesterdayApiTimes + "");
		int changeValue = todayApiTimes - yesterdayApiTimes;
		comparison.setDifferenceValue(changeValue + "");
		if (changeValue > 0) {
			comparison.setChangeStatus(1);
		} else if (changeValue < 0) {
			comparison.setChangeStatus(2);
		} else {
			comparison.setChangeStatus(0);
		}
		return comparison;
	}

	/**
	 * 获取日期对应的数据
	 * 
	 * @param dataMap 日期对应的数据
	 * @param day     日期（yyyy-MM-dd）
	 * @return int调用次数
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private int getApiTimes(Map<String, String> dataMap, String day) {
		String apiTimes = dataMap.get(day);
		if (!StringUtils.hasText(apiTimes)) {
			return 0;
		}
		return Integer.valueOf(apiTimes);
	}

	/**
	 * 按照时间分段获取统计接口调用的次数
	 * 
	 * @param condition 查询条件
	 * @param result    数据结果
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void getApiTimesByTimeGroup(DataBoardSearchParam condition, DataBoardEntity result) {
		List<DateTimeRange> dateTimeRangeList = getSearchApiTimesRange(condition);
		if (dateTimeRangeList == null || dateTimeRangeList.isEmpty()) {
			return;
		}

		List<DataBoardApiEntity> apiRangeList = new ArrayList<>(dateTimeRangeList.size());

		Query codeQuery = Query.of(q -> q.match(t -> t.field("resultCode").query("0000")));
		Query httpStatusQuery = Query.of(q -> q.match(t -> t.field("httpStatus").query(200)));

		for (DateTimeRange item : dateTimeRangeList) {
			String max = item.getMax().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			String min = item.getMin().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			try {
				// 所有调用次数
				Query rangeQuery = Query
						.of(q -> q.range(r -> r.field("createTime").gte(JsonData.of(min)).lt(JsonData.of(max))));

				CountResponse allCountQuery = elasticsearchClient
						.count(fn -> fn.index(ElasticsearchIndex.LOG_INDEX).query(rangeQuery));
				// 调用成功的次数
				CountResponse successCountQuery = elasticsearchClient.count(fn -> fn.index(ElasticsearchIndex.LOG_INDEX)
						.query(q -> q.bool(b -> b.must(rangeQuery, codeQuery, httpStatusQuery))));

				long allCount = allCountQuery.count();
				long successCount = successCountQuery.count();

				DataBoardApiEntity apiItem = new DataBoardApiEntity();
				apiItem.setApiStartDateTime(
						item.getMin().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE_TIME)));
				apiItem.setApiEndDateTime(
						item.getMax().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE_TIME)));
				apiItem.setApiTimes((int) allCount);
				apiItem.setApiSuccessTimes((int) successCount);
				apiItem.setApiFailedTimes((int) (allCount - successCount));
				apiRangeList.add(apiItem);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.setRecentApiData(apiRangeList);
	}

	/**
	 * 获取接口调用次数的范围
	 * 
	 * @param condition 查询条件
	 * @return List<IntegerRange> 范围列表
	 *
	 * @author zhouhui
	 * @since 2023.07.10
	 */
	public List<DateTimeRange> getSearchApiTimesRange(DataBoardSearchParam condition) {
		Integer interval = condition.getApiTimesInterval();
		if (interval == null || interval.intValue() <= 0 || interval.intValue() > 60) {
			return Collections.emptyList();
		}
		Integer count = condition.getApiTimesCount();
		if (count == null || count.intValue() <= 0) {
			return Collections.emptyList();
		}

		List<DateTimeRange> rangeList = new ArrayList<>(count);
		LocalDateTime curDateTime = LocalDateTime.now().withSecond(0).withNano(0);
		int curMinute = curDateTime.getMinute();

		DateTimeRange lastRange = new DateTimeRange();
		lastRange.setMax(curDateTime);

		if (curMinute % interval.intValue() == 0) {
			lastRange.setMin(curDateTime.minusMinutes(interval));
		} else {
			lastRange.setMin(curDateTime.minusMinutes(curMinute % interval.intValue()));
		}
		for (long i = count - 1L; i > 0; i--) {
			DateTimeRange rangeItem = new DateTimeRange();
			// 不包含最大值
			rangeItem.setMax(lastRange.getMin().minusMinutes(interval * (i - 1)));
			rangeItem.setMin(lastRange.getMin().minusMinutes(interval * i));
			rangeList.add(rangeItem);
		}
		rangeList.add(lastRange);
		return rangeList;
	}

	/**
	 * 按照服务类型统计接口调用的次数
	 * 
	 * @param today  今天日期
	 * @param result 数据结果
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void getApiTimesByService(String today, DataBoardEntity result) {
		Map<String, String> searchCondition = new HashedMap<>();

		String sql = "";
		sql += "SELECT serviceName, COUNT(path) apiTimes FROM " + ElasticsearchIndex.LOG_INDEX + " ";
		sql += "WHERE createDate = '" + today + "' ";
		sql += "GROUP BY serviceName ";
		searchCondition.put(QUERY_PARAM_NAME, sql);

		List<Map<String, String>> dataList = sqlSearchClient.executeSqlToMap(searchCondition);
		if (dataList == null) {
			return;
		}
		List<DataBoardServiceEntity> serviceList = new ArrayList<>(dataList.size());
		for (Map<String, String> item : dataList) {
			String serviceName = item.get("serviceName");
			String apiTimes = item.get("apiTimes");
			if (StringUtils.hasText(serviceName)) {
				DataBoardServiceEntity servieItem = new DataBoardServiceEntity();
				servieItem.setServiceName(serviceName);
				if (StringUtils.hasText(apiTimes)) {
					servieItem.setApiTimes(apiTimes);
				} else {
					servieItem.setApiTimes("0");
				}
				serviceList.add(servieItem);
			}
		}
		result.setServiceList(serviceList);
	}

	@Override
	public R<ServerBoardEntity> showServerInfo(ServerBoardSearchParam condition) {
		R<ServerBoardEntity> json = new R<>();
		long curTime = LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneOffset.UTC).toEpochSecond() * 1000L;
		String nameSpace = "acs_ecs_dashboard";

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// CPU使用信息
		ServerBoardEntity board = new ServerBoardEntity();
		board.setInstanceId(condition.getInstanceId());

		// 设置请求信息
		DescribeMetricLastRequest request = new DescribeMetricLastRequest();
		request.setNamespace(nameSpace);
		request.setPeriod("15");
		request.setStartTime(curTime + "");
		request.setEndTime(curTime + "");
		if (StringUtils.hasText(board.getInstanceId())) {
			request.setDimensions("[{\"instanceId\": \"" + board.getInstanceId() + "\"}]");
		}

		// 服务器基本信息
		serverBasicInfo(condition, board);

		// 获取CPU信息
		serverCpuInfo(request, board);

		// 内存信息
		serverMemoryInfo(request, board);

		// 服务器的磁盘信息
		serverDiskInfo(request, board);
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(board);
		return json;
	}

	/**
	 * 获取基础信息
	 * 
	 * @param condition 查询条件
	 * @param board     显示的数据信息
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void serverBasicInfo(ServerBoardSearchParam condition, ServerBoardEntity board) {
		DescribeInstanceAttributeRequest request = DescribeInstanceAttributeRequest.builder()
				.instanceId(condition.getInstanceId()).build();
		CompletableFuture<DescribeInstanceAttributeResponse> res = escClient.describeInstanceAttribute(request);
		try {
			DescribeInstanceAttributeResponse result = res.get(30, TimeUnit.SECONDS);
			if (result != null) {
				DescribeInstanceAttributeResponseBody body = result.getBody();
				if (body != null) {
					board.setMemorySize(body.getMemory());
					board.setCpu(body.getCpu());
					board.setStatus(body.getStatus());
					board.setRegionId(body.getRegionId());
					board.setBandwithOut(body.getInternetMaxBandwidthOut());
					board.setBandwithIn(body.getInternetMaxBandwidthIn());
				}
			}
		} catch (Exception e) {
			if(e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			e.printStackTrace();
		}
	}

	/**
	 * 获取服务器磁盘信息
	 * 
	 * @param request 接口请求参数
	 * @param board   显示的数据信息
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void serverDiskInfo(DescribeMetricLastRequest request, ServerBoardEntity board) {
		request.setMetricName("diskusage_total");

		try {
			DescribeMetricLastResponse res = cloudMonitorClient.describeMetricLast(request);
			if (res != null && HttpStatus.OK.value() == res.getStatusCode()) {
				String dataMsg = res.getBody().getDatapoints();
				List<ServerDiskEntity> dataList = mapper.readValue(dataMsg, new TypeReference<List<ServerDiskEntity>>() {
				});
				if (!dataList.isEmpty()) {
					Map<String, ServerDiskEntity> diskMap = new HashedMap<>();
					for(ServerDiskEntity item: dataList) {
						item.setSize(byteToGib(item.getAverage()));
						diskMap.put(item.getDevice(), item);
						item.setAverage(null);
					}
					board.setDiskList(dataList);
					
					//获取云盘/本地盘使用的信息
					serverDiskUseInfo(request, diskMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取服务器磁盘已使用信息
	 * 
	 * @param request 接口请求参数
	 * @param diskMap 云盘/硬盘基本信息
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void serverDiskUseInfo(DescribeMetricLastRequest request, Map<String, ServerDiskEntity> diskMap) {
		request.setMetricName("diskusage_used");
		
		try {
			DescribeMetricLastResponse res = cloudMonitorClient.describeMetricLast(request);
			if (res != null && HttpStatus.OK.value() == res.getStatusCode()) {
				String dataMsg = res.getBody().getDatapoints();
				List<ServerDiskEntity> dataList = mapper.readValue(dataMsg, new TypeReference<List<ServerDiskEntity>>() {
				});
				if (!dataList.isEmpty()) {
					for(ServerDiskEntity item: dataList) {
						ServerDiskEntity serverDiskItem = diskMap.get(item.getDevice());
						if(serverDiskItem != null) {
							Long average = byteToGib(item.getAverage());
							serverDiskItem.setUseSize(average);
							serverDiskItem.setUsageRate(Math.round(average.doubleValue() / serverDiskItem.getSize().doubleValue() * 100));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将byte转成gb
	 * @param source 原数据
	 * @return long 转换后的数据
	 *
	 * @author zhouhui
	 * @since 2023.07.15
	 */
	private long byteToGib(double source) {
		double temp = 1024;
		return (long)(Math.ceil(source / temp / temp / temp));
	}

	/**
	 * 获取服务器CPU信息
	 *
	 * @param request 接口请求参数
	 * @param board   汇总的服务器信息
	 * 
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void serverCpuInfo(DescribeMetricLastRequest request, ServerBoardEntity board) {
		request.setMetricName("cpu_total");

		try {
			DescribeMetricLastResponse res = cloudMonitorClient.describeMetricLast(request);
			if (res != null && HttpStatus.OK.value() == res.getStatusCode()) {
				String dataMsg = res.getBody().getDatapoints();
				List<ServerCpuEntity> dataList = mapper.readValue(dataMsg, new TypeReference<List<ServerCpuEntity>>() {
				});
				if (!dataList.isEmpty()) {
					board.setServerCpuEntity(dataList.get(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param request 接口请求参数
	 * @param board   汇总的服务器信息
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	private void serverMemoryInfo(DescribeMetricLastRequest request, ServerBoardEntity board) {
		request.setMetricName("memory_usedutilization");

		try {
			DescribeMetricLastResponse res = cloudMonitorClient.describeMetricLast(request);
			if (res != null && HttpStatus.OK.value() == res.getStatusCode()) {
				String dataMsg = res.getBody().getDatapoints();
				List<ServerMemoryEntity> dataList = mapper.readValue(dataMsg,
						new TypeReference<List<ServerMemoryEntity>>() {
						});
				if (!dataList.isEmpty()) {
					board.setServerMemoryEntity(dataList.get(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public R<List<ServerBasicEntity>> monitorData(ServerBoardSearchParam condition) {
		R<List<ServerBasicEntity>> json = new R<>();
		if(!StringUtils.hasText(condition.getMetricName())) {
			throw new CustomException("未设置查询类型");
		}
		
		LocalDateTime curTime = LocalDateTime.now(ZoneOffset.UTC);
		long endTime = curTime.atZone(ZoneOffset.UTC).toEpochSecond() * 1000L;
		long startTime = curTime.minusMinutes(60).atZone(ZoneOffset.UTC).toEpochSecond() * 1000L;
		String nameSpace = "acs_ecs_dashboard";
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		DescribeMetricListRequest request = new DescribeMetricListRequest();
		request.setNamespace(nameSpace);
		request.setMetricName(condition.getMetricName());
		request.setPeriod("300");
		request.setStartTime(startTime + "");
		request.setEndTime(endTime + "");
		if (StringUtils.hasText(condition.getInstanceId())) {
			request.setDimensions("[{\"instanceId\": \"" + condition.getInstanceId() + "\"}]");
		}
		
		List<ServerBasicEntity> dataList = null;
		try {
			DescribeMetricListResponse res = cloudMonitorClient.describeMetricList(request);
			if (res != null && HttpStatus.OK.value() == res.getStatusCode()) {
				String dataMsg = res.getBody().getDatapoints();
				dataList = mapper.readValue(dataMsg,new TypeReference<List<ServerBasicEntity>>() {});
				for(ServerBasicEntity item: dataList) {
					Instant instant = Instant.ofEpochMilli(item.getTimestamp());
					LocalDateTime showTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
					item.setShowTime(showTime);
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}
}
