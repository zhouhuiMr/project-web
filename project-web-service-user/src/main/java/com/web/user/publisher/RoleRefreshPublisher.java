package com.web.user.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import com.web.common.event.RoleRefreshEvent;

@Component
public class RoleRefreshPublisher implements ApplicationEventPublisherAware{
	
	private ApplicationEventPublisher publisher = null;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}
	
	
	/**
	 * 多个操作时是否需要等待。
	 * @param isWait 是否需要等待，true需要等待
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public void roleRefresh(boolean isWait) {
		roleRefresh(isWait, null);
	}

	/**
	 * 多个操作时需要进行等待。
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public void roleRefresh() {
		roleRefresh(null);
	}
	
	/**
	 * 根据角色ID更新缓存信息，多个操作时是否需要等待。
	 * @param isWait 是否需要等待，true需要等待
	 * @param roleId 角色的ID
	 *
	 * @author zhouhui
	 * @since 2022-11-05
	 */
	public void roleRefresh(boolean isWait, Integer roleId) {
		RoleRefreshEvent event = new RoleRefreshEvent(this,isWait);
		event.setRoleId(roleId);
		publisher.publishEvent(event);
	}
	
	/**
	 * 根据角色ID更新缓存信息，并发执行时进行等待。
	 * @param roleId 角色的ID
	 *
	 * @author zhouhui
	 * @since 2022-11-05
	 */
	public void roleRefresh(Integer roleId) {
		RoleRefreshEvent event = new RoleRefreshEvent(this,true);
		event.setRoleId(roleId);
		publisher.publishEvent(event);
	}
}
