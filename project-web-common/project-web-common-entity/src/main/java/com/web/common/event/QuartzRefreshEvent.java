package com.web.common.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuartzRefreshEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QuartzRefreshEvent(Object source) {
		super(source);
	}

	/** 任务执行的Id列表 */
	private List<Integer> execIdList;
	
	/** 操作类型：1、增加；2、停用；3、更新（停用然后增加） */
	private int operateType;
}
