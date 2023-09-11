package com.web.common.tool;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 时间处理的工具类
 * @author zhouhui
 * @since 1.0.0
 */
public class DateTimeUtil {

	private DateTimeUtil() {}
	
	/** yyyy-MM-dd */
	public static final String YEAR_MONTH_DATE= "yyyy-MM-dd";
	
	/** yyyy-MM-dd HH:mm:ss */
	public static final String YEAR_MONTH_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	
	/** yyyyMMddHHmmss */
	public static final String YEAR_MONTH_DATE_TIME_NO_CONNECTOR = "yyyyMMddHHmmss";
	
	/** HH:mm:ss */
	public static final String LOCAL_TIME_FORMATTER = "HH:mm:ss";
	
	/**
	 * 将LocalDate转成Date
	 * @param date LocalDate
	 * @return Date
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static Date localDateToDate(LocalDate date) {
		ZonedDateTime zonedDateTime = date.atStartOfDay().atZone(ZoneId.systemDefault());
		return Date.from(zonedDateTime.toInstant());
	}
	
	/**
	 * 将LocalDateTime转成Date
	 * @param date LocalDateTime
	 * @return Date
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static Date localDateTimeToDate(LocalDateTime dateTime) {
		ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
		return Date.from(zonedDateTime.toInstant());
	}
	
	/**
	 * 将Date转成对应格式字符串
	 * @param date Date
	 * @param format 日期格式字符串
	 * @return String 日期字符串
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String dateToFormatString(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateToFormatString(date, dateFormat);
	}
	
	/**
	 * 将Date转成对应格式字符串
	 * @param date Date
	 * @param dateFormat SimpleDateFormat日期格式对象
	 * @return String 日期字符串
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String dateToFormatString(Date date, SimpleDateFormat dateFormat) {
		return dateFormat.format(date);
	}
}
