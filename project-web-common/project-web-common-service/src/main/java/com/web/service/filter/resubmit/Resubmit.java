package com.web.service.filter.resubmit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resubmit {

	/**
	 * 重复提交的时间间隔（单位：秒）
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	long interval() default 5;
}
