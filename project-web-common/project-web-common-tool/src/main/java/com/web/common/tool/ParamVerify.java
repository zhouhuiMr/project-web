package com.web.common.tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * 字符串类型的判断
 * 使用正则表达式进行判断
 * 
 * */
public class ParamVerify {
	
	private ParamVerify() {}
	
	private static final String[] REGEX = {
		"^[0-9]*[1-9][0-9]*$",
		"^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$",
		"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$",
		"^(?![0-9]+$)(?![a-zA-Z]+$)(?![\\._@,]+$)[0-9A-Za-z\\._@,]{6,16}$"
	};
	
	/**
	 * 进行字符串的验证：</br>
	 * 0、正整数验证；</br>
	 * 1、手机号验证；</br>
	 * 2、邮箱地址验证；</br>
	 * 3、密码验证6-16位，字母加数字；</br>
	 * 
	 * @param i 说明中的编号
	 * @param str 需要验证的字符串
	 * @return boolean 通过返回true
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static boolean verify(int i,String str){
		boolean isRight = false;
		if(i > REGEX.length){
			return isRight;
		}
		isRight = Pattern.matches(REGEX[i], str);
		return isRight;
	}
	
	/**
	 * 校验密码格式。
	 * 包含数字、字母和,@_.特殊符号两两组合6-16个字符
	 * @param password 密码
	 * @return true 正确；false 错误
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static boolean verifyPassword(String password) {
		return Pattern.matches(REGEX[3], password);
	}
	
	/**
	 * 校验邮箱地址是否正确
	 * @param mail 邮箱地址
	 * @return true 正确；false 错误
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static boolean verifyMail(String mail) {
		return Pattern.matches(REGEX[2], mail);
	}
	
	/**
	 * 验证日期格式是否正确
	 * @param date 日期 格式 默认 yyyy-MM-dd
	 * @return boolean 正确返回true
	 * 
	 * @since 2018.11.04
	 * @author zhouhui
	 * */
	public static boolean verifyDateByStr(String date){
		boolean isRight = true;
		try {
			LocalDate.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT));
		} catch (Exception e) {
			return false;
		}
		return isRight;
	}
	
	/**
	 * 判断时间格式是否正确
	 * @param dateTime 日期 格式 默认 yyyy-MM-dd HH:mm:ss
	 * @return boolean 格式正确返回true
	 * 
	 * @since 2018.11.17
	 * @author zhouhui
	 * */
	public static boolean verifyDateTimeByStr(String dateTime){
		boolean isRight = true;
		try {
			LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT));
		} catch (Exception e) {
			return false;
		}
		return isRight;
	}
}
