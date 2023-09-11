package com.web.quartz.service.impl;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.basic.quartz.entity.QuartzJobExec;
import com.web.common.basic.quartz.entity.QuartzJobExecEntity;
import com.web.common.basic.quartz.entity.QuartzJobExecLog;
import com.web.common.basic.quartz.entity.QuartzJobNotify;
import com.web.quartz.mapper.QuartzJobExecLogMapper;
import com.web.quartz.mapper.QuartzJobExecMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *  项目启动            多个负载     单独维护定时任务
 *  +---+             +------+     +---+
 *  |   | ---         |SERVER| --> |MAP|
 *  +---+    | HTTP   +------+     +---+
 *            ------>  ......       
 *  +---+    |        +------+     +---+
 *  |   | ---         |SERVER| --> |MAP|
 *  +---+             +------+     +---+
 * 任务增删改 
 * </pre>
 * 
 * 在项目启动和任务的增删改时，会通过HTTP请求的方式（通过Spring事件驱动的方式执行）通知各个负载服务器。
 * 服务器在接收到消息后，会在维护的Map集合中进行定时任务的查询，如果查询到进行对应的操作。<br>
 * 
 * 定时任务执行时使用分布式锁控制，如果多台服务器同时执行一个任务，先获取到锁的任务会进行执行，其他服务器则跳过此任务。<br>
 * @author zhouhui
 * @since 1.0.0
 */
@Slf4j
@Component
public class QuartzJobManage {

	/** 定时任务存储的信息 */
	private static final Map<Integer, ScheduledFuture<?>> QUARTZ_JOB_MAP = new HashMap<>();

	private static final Object JOBLOCK = new Object();

	private static final String QUARTZ_LOCK_PREFIX = "QUARTZ::LOCK::";

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private QuartzJobExecMapper quartzJobExecMapper;
	
	@Autowired
	private QuartzJobExecLogMapper quartzJobExecLogMapper;

	@Autowired
	private RestTemplate httpTemplate;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private RedissonClient redissonClient;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Async("taskScheduler")
	public void quartzJobManage(QuartzJobNotify notify) {
		List<QuartzJobExecEntity> jobList = null;
		
		if(notify.getOperateType() == 2) {
			//停用时不需要判断数据库的状态，直接将管理的任务删除掉即可（因为删除的数据数据库查询不到，不能查询数据库）。
			jobList = new ArrayList<>();
			for(Integer item: notify.getExecIdList()) {
				QuartzJobExecEntity exec = new QuartzJobExecEntity();
				exec.setExecId(item);
				jobList.add(exec);
			}
		}else {
			Page<QuartzJobExec> page = new Page<>(1, -1);
			QuartzJobExecEntity condition = new QuartzJobExecEntity();
			condition.setExecIdList(notify.getExecIdList());
			condition.setJobStatus(0);
			IPage<QuartzJobExecEntity> result = quartzJobExecMapper.getJobList(page, condition);
			if (result == null || result.getRecords() == null || result.getRecords().isEmpty()) {
				return;
			}
			jobList = result.getRecords();
		}
		

		synchronized (JOBLOCK) {
			if (notify.getOperateType() == 1) {
				// 增加
				addJob(jobList);
			} else if (notify.getOperateType() == 2) {
				// 停用
				cancelJob(jobList);
			} else {
				// 更新
				cancelJob(jobList);
				addJob(jobList);
			}
		}
	}

	/**
	 * 取消任务
	 * 
	 * @param jobList 任务列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private void cancelJob(List<QuartzJobExecEntity> jobList) {
		for (QuartzJobExecEntity item : jobList) {
			ScheduledFuture<?> future = QUARTZ_JOB_MAP.get(item.getExecId());
			if (future != null) {
				future.cancel(true);
				QUARTZ_JOB_MAP.remove(item.getExecId());
			}
		}
	}

	/**
	 * 新增任务
	 * 注：如果任务信息已经存在，则不进行任务的新增。
	 * @param jobList 任务列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private void addJob(List<QuartzJobExecEntity> jobList) {
		for (QuartzJobExecEntity item : jobList) {
			if(QUARTZ_JOB_MAP.get(item.getExecId()) != null) {
				continue;
			}
			
			ScheduledFuture<?> future = null;
			if (item.getExecPeriod() == 0) {
				// 固定频率
				future = taskScheduler.scheduleAtFixedRate(() -> jobRun(item, 0), Long.valueOf(item.getExecPeriodValue()));
			} else {
				// cron
				future = taskScheduler.schedule(() -> jobRun(item, 0), new CronTrigger(item.getExecPeriodValue()));
			}
			QUARTZ_JOB_MAP.put(item.getJobId(), future);
		}
	}

	/**
	 * 定时任务实际执行
	 * 
	 * @param job 任务信息
	 * @param triggerMode 触发方式，0自动；1手动
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public void jobRun(QuartzJobExecEntity job, int triggerMode) {
		QuartzJobExecLog execLog = new QuartzJobExecLog();
		execLog.setExecId(job.getExecId());
		execLog.setTriggerMode(triggerMode);
		execLog.setExecPeriod(job.getExecPeriod());
		execLog.setExecPeriodValue(job.getExecPeriodValue());
		
		RLock lock = redissonClient.getLock(QUARTZ_LOCK_PREFIX + job.getExecId());
		if (lock.isLocked()) {
			return;
		}
		try {
			lock.lock(120, TimeUnit.SECONDS);
			if (job.getExecMode() == 0) {
				// http请求
				quartzHttpRun(job, execLog);
			} else {
				// bean方式
				quartzBeanRun(job, execLog);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		//记录日志信息
		execLog.setEndTime(LocalDateTime.now());
		execLog.setCreateTime(LocalDateTime.now());
		quartzJobExecLogMapper.insert(execLog);
	}

	/**
	 * 通过http的方式进行执行
	 * 
	 * @param job 任务信息
	 * @param execLog 执行的日志信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private void quartzHttpRun(QuartzJobExecEntity job, QuartzJobExecLog execLog) {
		execLog.setStartTime(LocalDateTime.now());
		
		HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.CONTENT_TYPE, job.getHttpContentType());
		HttpEntity<String> httpEntity = null;
		if (StringUtils.hasText(job.getExecParam())) {
			httpEntity = new HttpEntity<>(job.getExecParam(), header);
		}else {
			httpEntity = new HttpEntity<>(header);
		}
		 
		HttpMethod httpMethod = HttpMethod.resolve(job.getHttpMethod().toUpperCase());
		if (httpMethod == null) {
			httpMethod = HttpMethod.POST;
		}
		ResponseEntity<Object> response = httpTemplate.exchange(job.getHttpUrl(), httpMethod, httpEntity, Object.class);
		if (response.hasBody()) {
			String message = "";
			try {
				message = OBJECT_MAPPER.writeValueAsString(response.getBody());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			String temp = "定时任务HTTP执行方法：" + response.getStatusCodeValue() + "," + message;
			int saveResultLength = 500;
			if(temp.length() > saveResultLength) {
				execLog.setExecResult(temp.substring(0, saveResultLength));
			}else {
				execLog.setExecResult(temp);
			}
			log.info(temp);
		} else {
			String message = "定时任务HTTP执行方法：" + response.getStatusCodeValue();
			execLog.setExecResult(message);
			log.info(message);
		}
	}

	/**
	 * 通过Bean的方式进行执行
	 * 
	 * @param job 任务信息
	 * @param execLog 执行的日志信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private void quartzBeanRun(QuartzJobExecEntity job, QuartzJobExecLog execLog) {
		execLog.setStartTime(LocalDateTime.now());
		
		Object bean = null;
		try {
			bean = applicationContext.getBean(job.getBeanName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bean == null) {
			String message = "定时任务Bean方式执行，没有查询到对应的Bean";
			execLog.setExecResult(message);
			log.info(message);
			return;
		}
		Method[] methods = bean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			// 一定得是Public方法，并且参数最多一个
			if (method.getName().equals(job.getBeanMethod()) && method.getParameterCount() <= 1) {
				try {
					if (method.getParameterCount() == 0) {
						method.invoke(bean);
					} else {
						Class<?> cls = method.getParameterTypes()[0];
						Object objectType = OBJECT_MAPPER.readValue(job.getExecParam(), cls);
						method.invoke(bean, objectType);
						String message = "定时任务Bean方式执行，执行完成";
						execLog.setExecResult(message);
						log.info(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
