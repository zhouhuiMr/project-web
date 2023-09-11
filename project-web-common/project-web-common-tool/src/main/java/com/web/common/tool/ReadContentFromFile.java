package com.web.common.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * 从文件中读取文件内容，
 * 然后替换${}中的变量对应的值，
 * 如${name}替换成name:test中的test
 * 
 * @author zhouhui
 * @since 1.0.0
 */
public class ReadContentFromFile {
	
	private ReadContentFromFile() {}
	
	/**
	 * 读取文件中的内容，并且将占位符换成对应的数据
	 * @param path 文件路径
	 * @param data 占位符名称对应的数据
	 * @return String 将占位符换成数据的文件内容
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String getContent(String path,Map<String, String> data) {
		String content = getContentFromFile(path);
		return replaceChar(content, data);
	}
	
	/**
	 * 从文件中读取内容
	 * @param path 文件的路径
	 * @return String 文件的内容
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String getContentFromFile(String path) {
		StringBuilder content = new StringBuilder();
		
		File file = new File(path); 
		
		InputStreamReader reader = null;
		BufferedReader contentReader = null;
		try (FileInputStream inStream = new FileInputStream(file)) {
			reader = new InputStreamReader(inStream,StandardCharsets.UTF_8);
			contentReader = new BufferedReader(reader);
			
			String line = null;
			while ((line = contentReader.readLine()) != null) {
				content.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(contentReader != null) {
				try {
					contentReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return content.toString();
	}
	
	
	/**
	 * 替换文件中的定义的字符。
	 * 将data中的属性用${}进行处理，如${name}
	 * @param content 字符串内容
	 * @param data 需要替换的数据属性
	 * 
	 * @author zhouhui
	 * @since 2021.06.27
	 */
	public static String replaceChar(String content,Map<String, String> data) {
		if(data == null) {
			return null;
		}
		if(content == null || "".equals(content)) {
			return null;
		}
		
		Iterator<String> keyIter = data.keySet().iterator();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			String temp = "${" + key + "}";
			
			String value = data.get(key);
			
			content = content.replace(temp, value);
		}
		return content;
	}
}
