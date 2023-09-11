package com.web.common.basic.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateTimeRange {

	/** 最小时间 */
	private LocalDateTime min;

	/** 最大时间 */
	private LocalDateTime max;
	
	public DateTimeRange() {}
	
	public DateTimeRange(LocalDateTime min, LocalDateTime max) {
		this.min = min;
		this.max = max;
	}
}
