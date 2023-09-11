package com.web.common.dictionary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记对象中的字典项的属性。
 * 功能：
 * <ol>
 * 1、校验字段对应字典中value属性是否存在；
 * <p>
 * 2、根据label中文描述转成对应value值；
 * <p>
 * 3、根据value值转成label对应的中文描述。
 * </ol>
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD})
public @interface VerifyDictField {
	
	/**
	 * OperateMode.VERIFY 校验字段对应字典中value属性是否存在。
	 * <p>
	 * OperateMode.LABEL2VALUE 根据label中文描述转成对应value值。
	 * <p>
	 * OperateMode.VALUE2LABEL 根据value值转成label对应的中文描述。
	 * 
	 * @since 1.0.0
	 */
	OperateMode operateMode(); 
	
	/**
	 * 字典类型type
	 * 
	 * @since 1.0.0
	 */
	String dictType();
	
	/**
	 * 是否必须有值，true如果没有值会报错，false如果没有则不会报错
	 * 
	 * @since 1.0.0
	 */
	boolean required() default false;
	
	/**
	 * OperateMode.LABEL2VALUE和OperateMode.VALUE2LABEL
	 * 转换后存储数据的字段。
	 * 
	 * @since 1.0.0
	 */
	String storeFieldName() default "";
	
	
	enum OperateMode {
		VERIFY,
		LABEL2VALUE,
		VALUE2LABEL
	}
}
