package com.web.quartz.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import com.web.common.event.QuartzRefreshEvent;

@Component
public class QuartzJobPublisher implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		publisher = applicationEventPublisher;
	}

	/**
	 * 新增定时任务
	 * 
	 * @param execId 任务的执行Id
	 *
	 * @author zhouhui
	 * @since 2023.08.11
	 */
	public void addQuartzJob(Integer execId) {
		List<Integer> execIdList = new ArrayList<>();
		execIdList.add(execId);
		QuartzRefreshEvent event = new QuartzRefreshEvent(this);
		event.setExecIdList(execIdList);
		event.setOperateType(1);
		publisher.publishEvent(event);
	}

	/**
	 * 删除定时任务
	 * 
	 * @param execIdList 任务的执行Id列表
	 *
	 * @author zhouhui
	 * @since 2023.08.11
	 */
	public void deleteQuartzJob(List<Integer> execIdList) {
		QuartzRefreshEvent event = new QuartzRefreshEvent(this);
		event.setExecIdList(execIdList);
		event.setOperateType(2);
		publisher.publishEvent(event);
	}

	/**
	 * 更新定时任务
	 * @param execIdList 任务的执行Id列表
	 *
	 * @author zhouhui
	 * @since 2023.08.11
	 */
	public void updateQuartzJob(List<Integer> execIdList) {
		QuartzRefreshEvent event = new QuartzRefreshEvent(this);
		event.setExecIdList(execIdList);
		event.setOperateType(3);
		publisher.publishEvent(event);
	}

	/**
	 * 新增系统的所有定时任务
	 * 
	 * @author zhouhui
	 * @since 2023.08.11 
	 */
	public void addAllQuartzJob() {
		QuartzRefreshEvent event = new QuartzRefreshEvent(this);
		event.setExecIdList(new ArrayList<>());
		event.setOperateType(1);
		publisher.publishEvent(event);
	}
}
