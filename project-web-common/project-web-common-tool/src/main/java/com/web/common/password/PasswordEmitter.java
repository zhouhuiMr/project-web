package com.web.common.password;

import java.util.Random;

public class PasswordEmitter {
	
	private static final String PASSWORD_CODE = "23456789abcdefghjklmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	private static Random random = new Random();
	
	private PasswordEmitter() {
		
	}

	/**
	 * 生成随机的密码
	 * @return 
	 *
	 * @author zhouhui
	 * @since 2021.06.20 
	 */
	public static String getPassword(int length) {
		StringBuilder password = new StringBuilder();
		if(length <= 0) {
			return "";
		}
		for(int i = 0;i < length;i++) {
			int beginIndex = random.nextInt(PASSWORD_CODE.length());
			password.append(PASSWORD_CODE.substring(beginIndex, beginIndex+1));
		}
		return password.toString();
	}
}
