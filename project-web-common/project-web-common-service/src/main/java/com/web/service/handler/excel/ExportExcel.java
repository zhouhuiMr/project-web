package com.web.service.handler.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 在controller上标记导出Excel数据。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface ExportExcel {

	
	/**
	 * 导出excel的文件名
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	String fileName();
	
	/**
	 * 导出excel的sheet名称
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	String sheetName() default "";
}
