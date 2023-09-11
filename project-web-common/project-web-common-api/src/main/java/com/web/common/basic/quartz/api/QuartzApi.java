package com.web.common.basic.quartz.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.quartz.entity.QuartzJobExecEntity;
import com.web.common.basic.quartz.entity.QuartzJobNotify;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "quartzApi", path = "/basic/quartz")
public interface QuartzApi {

	/**
	 * 获取定时任务列表
	 * 
	 * @param condition 查询条件
	 * @return R<IPage<QuartzJobEntity>> 定时任务列表
	 *
	 * @author zhouhui
	 * @since 2023.08.08
	 */
	@PostMapping(value = "/getJobList")
	@ResponseBody
	R<IPage<QuartzJobExecEntity>> getJobList(@RequestBody QuartzJobExecEntity condition);

	/**
	 * 保存定时任务信息
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.09
	 */
	@PostMapping(value = "/save")
	@ResponseBody
	R<Object> saveQuartzJob(@RequestBody QuartzJobExecEntity job);

	/**
	 * 更新定时任务信息
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.08.09
	 */
	@PostMapping(value = "/update")
	@ResponseBody
	R<Object> updateQuartzJob(@RequestBody QuartzJobExecEntity job);

	/**
	 * 删除定时任务
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.09
	 */
	@PostMapping(value = "/delete")
	@ResponseBody
	R<Object> deleteQuartzJob(@RequestBody QuartzJobExecEntity job);

	/**
	 * 通知定时任务进行新增更新
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.12
	 */
	@PostMapping(value = "/notify")
	@ResponseBody
	R<Object> notifyQuartzJob(@RequestBody QuartzJobNotify job);

	/**
	 * 停用或者启用定时任务
	 * @param job 停用或启用的定时任务
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.14 
	 */
	@PostMapping(value = "/runOrStop")
	@ResponseBody
	R<Object> runOrStopQuartzJob(@RequestBody QuartzJobExecEntity job);
	
	/**
	 * 执行一次任务
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.15 
	 */
	@PostMapping(value = "/executeOnce")
	@ResponseBody
	R<Object> executeOnceQuartzJob(@RequestBody QuartzJobExecEntity job);
}
