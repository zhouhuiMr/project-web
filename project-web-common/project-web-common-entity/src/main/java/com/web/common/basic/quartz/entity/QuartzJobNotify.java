package com.web.common.basic.quartz.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuartzJobNotify {

	/** 任务执行的Id列表 */
	private List<Integer> execIdList;
	
	/** 操作类型：1、增加；2、停用；3、更新（停用然后增加） */
	private int operateType;
}
