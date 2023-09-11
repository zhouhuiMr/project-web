package com.web.config.init;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * 按照elasticsearch的index命名json文件，<br>
 * 并且将json文件放到/resources/elasticsearch目录下。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class ElasticsearchMapping implements CommandLineRunner {

	/** index对应mapping的json结构 */
	private static final Map<String, String> JSON_INDEX = new HashedMap<>();

	/** 文件名称列表 */
	private static final String[] FILE_NAME_LIST = { "project_web_log" };

	@Override
	public void run(String... args) throws Exception {
		for (int i = 0; i < FILE_NAME_LIST.length; i++) {
			String fileName = FILE_NAME_LIST[i];
			String jsonStr = getStringFromJson("/elasticsearch/" + fileName + ".json");
			JSON_INDEX.put(fileName, jsonStr);
		}
	}

	/**
	 * 从JSON文件读取内容
	 * 
	 * @param path 文件路径
	 * @return String 文件内容
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private String getStringFromJson(String path) {
		ClassPathResource resource = new ClassPathResource(path);
		StringBuilder builder = new StringBuilder();

		try (FileReader fileReader = new FileReader(resource.getFile());
				BufferedReader bufferedReader = new BufferedReader(fileReader);) {
			String str = "";
			while ((str = bufferedReader.readLine()) != null) {
				builder.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * 获取存储的index
	 * 
	 * @return Map<String, String> 存储的数据
	 *
	 * @author zhouhui
	 * @since 2023.07.01
	 */
	public Map<String, String> getJsonIndex() {
		return JSON_INDEX;
	}
}
