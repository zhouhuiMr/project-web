package com.web.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.basic.quartz.entity.QuartzJob;
import com.web.common.basic.quartz.entity.QuartzJobExec;
import com.web.common.basic.quartz.entity.QuartzJobExecEntity;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.quartz.mapper.QuartzJobExecMapper;
import com.web.quartz.mapper.QuartzJobMapper;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 定时任务名称 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Service
public class QuartzJobServiceImpl extends ServiceImpl<QuartzJobMapper, QuartzJob> {

	@Autowired
	private QuartzJobExecMapper quartzJobExecMapper;

	@Autowired
	private PlatformTransactionManager txManagerOne;

	@Autowired
	private TransactionDefinition transactionDefinition;

	@Autowired
	private QuartzJobPublisher quartzJobPublisher;
	
	@Autowired
	private QuartzJobManage quartzJobManage;

	/**
	 * 查询定时任务数据列表
	 * 
	 * @param condition 查询条件
	 * @return R<IPage<QuartzJobExecEntity>> 数据列表
	 *
	 * @author zhouhui
	 * @since 2023.08.09
	 */
	public R<IPage<QuartzJobExecEntity>> getJobList(QuartzJobExecEntity condition) {
		R<IPage<QuartzJobExecEntity>> json = new R<>();
		setSearchCondition(condition);
		Page<QuartzJobExec> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<QuartzJobExecEntity> dataList = quartzJobExecMapper.getJobList(page, condition);
		if (dataList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}

	/**
	 * 新增定时任务信息。 注：针对HTTP请求地址是否正确，Bean是否存在，在实际执行时再进行校验。
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.11
	 */
	public R<Object> saveQuartzJob(QuartzJobExecEntity job) {
		R<Object> json = new R<>();
		// 校验信息
		checkParam(job);

		WebUser user = UserUtil.getUser();

		TransactionStatus status = txManagerOne.getTransaction(transactionDefinition);
		try {
			QuartzJob quartzJob = new QuartzJob();
			quartzJob.setJobName(job.getJobName());
			quartzJob.setJobRemark(job.getJobRemark());
			quartzJob.setCreateUserId(user.getUserId());
			quartzJob.setChangeTime(LocalDateTime.now());
			this.baseMapper.insert(quartzJob);

			QuartzJobExec exec = new QuartzJobExec();
			BeanUtils.copyProperties(job, exec);
			exec.setJobId(quartzJob.getJobId());
			exec.setCreateUserId(user.getUserId());
			exec.setCreateTime(LocalDateTime.now());
			quartzJobExecMapper.insert(exec);

			job.setExecId(exec.getExecId());
			txManagerOne.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			txManagerOne.rollback(status);
		}

		// 如果任务启用，则通知新增定时任务
		if (job.getJobStatus().intValue() == 0) {
			quartzJobPublisher.addQuartzJob(job.getExecId());
		}

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 更新定时任务信息
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.14
	 */
	public R<Object> updateQuartzJob(QuartzJobExecEntity job) {
		R<Object> json = new R<>();
		// 校验信息
		checkParam(job);

		WebUser user = UserUtil.getUser();

		// 查询之前的数据
		QuartzJobExec jobExec = quartzJobExecMapper.selectById(job.getExecId());

		TransactionStatus status = txManagerOne.getTransaction(transactionDefinition);
		try {
			QuartzJob quartzJob = new QuartzJob();
			quartzJob.setJobName(job.getJobName());
			quartzJob.setJobRemark(job.getJobRemark());
			quartzJob.setChangeUserId(user.getUserId());
			quartzJob.setChangeTime(LocalDateTime.now());
			LambdaUpdateWrapper<QuartzJob> jobUpdate = new LambdaUpdateWrapper<>();
			jobUpdate.eq(QuartzJob::getJobId, jobExec.getJobId());
			jobUpdate.eq(QuartzJob::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			this.baseMapper.update(quartzJob, jobUpdate);

			QuartzJobExec exec = new QuartzJobExec();
			BeanUtils.copyProperties(job, exec);
			exec.setChangeUserId(user.getUserId());
			exec.setChangeTime(LocalDateTime.now());
			exec.setJobId(null);
			exec.setDelFlag(null);
			exec.setCreateUserId(null);
			exec.setCreateTime(null);
			LambdaUpdateWrapper<QuartzJobExec> execUpdate = new LambdaUpdateWrapper<>();
			execUpdate.eq(QuartzJobExec::getExecId, job.getExecId());
			execUpdate.eq(QuartzJobExec::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			if(job.getExecMode() == 0) {
				//http方式，清空bean的数据
				execUpdate.set(QuartzJobExec::getBeanMethod, null);
				execUpdate.set(QuartzJobExec::getBeanName, null);
			}else {
				//bean方式，清空http的数据
				execUpdate.set(QuartzJobExec::getHttpUrl, null);
				execUpdate.set(QuartzJobExec::getHttpMethod, null);
				execUpdate.set(QuartzJobExec::getHttpContentType, null);
			}
			quartzJobExecMapper.update(exec, execUpdate);

			if (exec.getJobStatus() != null) {
				jobExec.setJobStatus(exec.getJobStatus());
			}

			txManagerOne.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			txManagerOne.rollback(status);
		}

		if (jobExec.getJobStatus().intValue() == 0) {
			List<Integer> execIdList = new ArrayList<>();
			execIdList.add(jobExec.getExecId());
			quartzJobPublisher.updateQuartzJob(execIdList);
		}

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 删除定时任务信息
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.14
	 */
	public R<Object> deleteQuartzJob(QuartzJobExecEntity job) {
		R<Object> json = new R<>();

		WebUser user = UserUtil.getUser();

		// 查询之前的数据
		QuartzJobExec jobExec = quartzJobExecMapper.selectById(job.getExecId());
		boolean isDelSuccess = false;

		TransactionStatus status = txManagerOne.getTransaction(transactionDefinition);
		try {
			LambdaUpdateWrapper<QuartzJobExec> execUpdate = new LambdaUpdateWrapper<>();
			execUpdate.eq(QuartzJobExec::getExecId, job.getExecId());
			execUpdate.eq(QuartzJobExec::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			execUpdate.set(QuartzJobExec::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
			execUpdate.set(QuartzJobExec::getChangeUserId, user.getUserId());
			execUpdate.set(QuartzJobExec::getChangeTime, LocalDateTime.now());
			int execDelRow = quartzJobExecMapper.update(null, execUpdate);
			if (execDelRow != 1) {
				throw new CustomException("删除可执行的定时任务失败");
			}

			LambdaUpdateWrapper<QuartzJob> jobUpdate = new LambdaUpdateWrapper<>();
			jobUpdate.eq(QuartzJob::getJobId, jobExec.getJobId());
			jobUpdate.eq(QuartzJob::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			jobUpdate.set(QuartzJob::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
			jobUpdate.set(QuartzJob::getChangeUserId, user.getUserId());
			jobUpdate.set(QuartzJob::getChangeTime, LocalDateTime.now());
			int jobDelRow = this.baseMapper.update(null, jobUpdate);
			if (jobDelRow != 1) {
				throw new CustomException("删除定时任务描述信息失败");
			}
			txManagerOne.commit(status);
			isDelSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			txManagerOne.rollback(status);
		}

		if (isDelSuccess) {
			List<Integer> execIdList = new ArrayList<>();
			execIdList.add(jobExec.getExecId());
			quartzJobPublisher.deleteQuartzJob(execIdList);
		}

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 启用或者停用定时任务
	 * 
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.14
	 */
	public R<Object> runOrStopQuartzJob(QuartzJobExecEntity job) {
		R<Object> json = new R<>();

		WebUser user = UserUtil.getUser();

		LambdaUpdateWrapper<QuartzJobExec> execUpdate = new LambdaUpdateWrapper<>();
		execUpdate.eq(QuartzJobExec::getExecId, job.getExecId());
		execUpdate.eq(QuartzJobExec::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		execUpdate.set(QuartzJobExec::getJobStatus, job.getJobStatus());
		execUpdate.set(QuartzJobExec::getChangeUserId, user.getUserId());
		execUpdate.set(QuartzJobExec::getChangeTime, LocalDateTime.now());
		int execDelRow = quartzJobExecMapper.update(null, execUpdate);
		if (execDelRow != 1) {
			throw new CustomException("更改设备的启用停用状态失败");
		}
		if (job.getJobStatus().intValue() == 0) {
			List<Integer> execIdList = new ArrayList<>();
			execIdList.add(job.getExecId());
			quartzJobPublisher.updateQuartzJob(execIdList);
		} else if (job.getJobStatus().intValue() == 1) {
			List<Integer> execIdList = new ArrayList<>();
			execIdList.add(job.getExecId());
			quartzJobPublisher.deleteQuartzJob(execIdList);
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	/**
	 * 手动触发执行一次定时任务。
	 * @param job 任务信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.08.15 
	 */
	public R<Object> executeOnceQuartzJob(QuartzJobExecEntity job) {
		R<Object> json = new R<>();
		
		LambdaQueryWrapper<QuartzJobExec> query = new LambdaQueryWrapper<>();
		query.eq(QuartzJobExec::getExecId, job.getExecId());
		query.eq(QuartzJobExec::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<QuartzJobExec> jobList = quartzJobExecMapper.selectList(query);
		if(jobList == null || jobList.isEmpty()) {
			throw new CustomException("没有查询到任务信息");
		}
		QuartzJobExec execJob = jobList.get(0);
		BeanUtils.copyProperties(execJob, job);
		
		quartzJobManage.jobRun(job, 1);
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 校验上传的参数是否正确
	 * 
	 * @param job 任务信息
	 *
	 * @author zhouhui
	 * @since 2023.08.11
	 */
	private void checkParam(QuartzJobExecEntity job) {
		if (job.getExecMode().intValue() == 0) {
			// http请求
			if (!StringUtils.hasText(job.getHttpMethod()) || !StringUtils.hasText(job.getHttpContentType()) || !StringUtils.hasText(job.getHttpUrl())) {
				throw new CustomException("未设置HTTP请求信息");
			}
			job.setBeanName(null);
			job.setBeanMethod(null);
		} else {
			// bean方式
			if (!StringUtils.hasText(job.getBeanName()) || !StringUtils.hasText(job.getBeanMethod())) {
				throw new CustomException("未设置调用方法信息");
			}
			job.setHttpMethod(null);
			job.setHttpUrl(null);
			job.setHttpContentType(null);
		}
		if (job.getJobStatus() == null || (job.getJobStatus() != 1 && job.getJobStatus() != 0)) {
			throw new CustomException("未设置任务的状态信息");
		}
		if(job.getExecPeriod().intValue() == 1 && !CronExpression.isValidExpression(job.getExecPeriodValue())) {
			throw new CustomException("cron表达式格式错误");
		}
	}

	/**
	 * 设置查询条件
	 * 
	 * @param condition 查询条件
	 *
	 * @author zhouhui
	 * @since 2023.08.09
	 */
	private void setSearchCondition(QuartzJobExecEntity condition) {
		if (StringUtils.hasText(condition.getJobName())) {
			condition.setJobName("%" + condition.getJobName() + "%");
		}
	}
}
