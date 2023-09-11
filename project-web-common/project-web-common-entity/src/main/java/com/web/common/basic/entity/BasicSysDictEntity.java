package com.web.common.basic.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicSysDictEntity extends BasicSysDict{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 当前页数 */
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	private int size = 10;
}
