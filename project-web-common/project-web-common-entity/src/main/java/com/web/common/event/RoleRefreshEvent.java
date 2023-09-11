package com.web.common.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRefreshEvent extends ApplicationEvent{

	private static final long serialVersionUID = 1L;
	
	/** 是否等待，true等待，false不等待 */
	private boolean isWait = true;

	public RoleRefreshEvent(Object source, boolean isWait) {
		super(source);
		this.isWait = isWait;
	}
	
	/** 角色的ID */
	private Integer roleId;
}
