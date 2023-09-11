package com.web.quartz.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.basic.quartz.api.QuartzApi;
import com.web.common.basic.quartz.entity.QuartzJobExecEntity;
import com.web.common.basic.quartz.entity.QuartzJobNotify;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.quartz.service.impl.QuartzJobManage;
import com.web.quartz.service.impl.QuartzJobServiceImpl;
import com.web.service.filter.resubmit.Resubmit;
import com.web.service.handler.error.CustomException;
import com.web.service.handler.log.IgnoreOperateLogs;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 定时任务名称 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Tag(name = "定时任务")
@Controller
@RequestMapping("/basic/quartz")
public class QuartzJobController implements QuartzApi {

	@Autowired
	private QuartzJobServiceImpl quartzJobServiceImpl;
	
	@Autowired
	private QuartzJobManage quartzJobManage;

	@Operation(summary = "获取定时任务列表")
	@Override
	public R<IPage<QuartzJobExecEntity>> getJobList(QuartzJobExecEntity condition) {
		return quartzJobServiceImpl.getJobList(condition);
	}

	@Resubmit
	@Operation(summary = "保存定时任务")
	@Override
	public R<Object> saveQuartzJob(@Valid QuartzJobExecEntity job) {
		return quartzJobServiceImpl.saveQuartzJob(job);
	}

	@Operation(summary = "更新定时任务")
	@Override
	public R<Object> updateQuartzJob(@Valid QuartzJobExecEntity job) {
		if(job.getExecId() == null) {
			throw new CustomException("未选择要更新的定时任务");
		}
		return quartzJobServiceImpl.updateQuartzJob(job);
	}

	@Operation(summary = "删除定时任务")
	@Override
	public R<Object> deleteQuartzJob(QuartzJobExecEntity job) {
		if(job.getExecId() == null) {
			throw new CustomException("未选择要删除的定时任务");
		}
		return quartzJobServiceImpl.deleteQuartzJob(job);
	}

	@IgnoreOperateLogs
	@Operation(summary = "通知定时任务进行新增更新")
	@Override
	public R<Object> notifyQuartzJob(QuartzJobNotify notify){
		R<Object> json = new R<>();
		quartzJobManage.quartzJobManage(notify);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Operation(summary = "停用或者启用定时任务")
	@Override
	public R<Object> runOrStopQuartzJob(QuartzJobExecEntity job) {
		if(job.getExecId() == null) {
			throw new CustomException("未选择要启用或者停用的定时任务");
		}
		if (job.getJobStatus() == null || (job.getJobStatus() != 1 && job.getJobStatus() != 0)) {
			throw new CustomException("未设置任务的状态信息");
		}
		return quartzJobServiceImpl.runOrStopQuartzJob(job);
	}

	@Operation(summary = "执行一次定时任务")
	@Override
	public R<Object> executeOnceQuartzJob(QuartzJobExecEntity job) {
		if(job.getExecId() == null) {
			throw new CustomException("未选择要执行的定时任务");
		}
		return quartzJobServiceImpl.executeOnceQuartzJob(job);
	}
}
