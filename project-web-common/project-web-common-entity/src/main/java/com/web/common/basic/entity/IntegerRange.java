package com.web.common.basic.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegerRange {

	/** 最小值 */
	private Integer min;
	
	/** 最大值 */
	private Integer max;
	
	public IntegerRange(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * [a, b) 
	 * @param value 输入值
	 * @return 是否包含输入的值
	 *
	 * @author zhouhui
	 * @since 2023.07.10 
	 */
	public boolean leftContains(int value) {
		return ( min.intValue() <= value ) && ( max.intValue() > value );
	}
	
	/**
	 * (a, b] 
	 * @param value 输入值
	 * @return 是否包含输入的值
	 *
	 * @author zhouhui
	 * @since 2023.07.10 
	 */
	public boolean rightContains(int value) {
		return ( min.intValue() < value ) && ( max.intValue() >= value );
	}
	
	/**
	 * [a, b] 
	 * @param value 输入值
	 * @return 是否包含输入的值
	 *
	 * @author zhouhui
	 * @since 2023.07.10 
	 */
	public boolean contains(int value) {
		return ( min.intValue() <= value ) && ( max.intValue() >= value );
	}
}
