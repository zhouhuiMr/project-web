package com.web.common.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class FileUtil {
	
	private FileUtil() {}

	/**
	 * 将文件路径按照“/”拆分。
	 * @param path 文件路径
	 * @return List<String> 拆分后的路径列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static List<String> getFilePath(String path) {
		if (!StringUtils.hasText(path)) {
			return new ArrayList<>();
		}
		path = path.replace(" ", "");
		String[] strs = path.split(Symbols.FORWARD_SLASH);
		if (strs == null || strs.length == 0) {
			return new ArrayList<>();
		}
		List<String> pathList = new ArrayList<>();
		for (int i = 0; i < strs.length; i++) {
			String temp = strs[i].replace(Symbols.SPACE, "");
			if("".equals(temp)) {
				continue;
			}
			pathList.add(temp);
		}
		return pathList;
	}
	
	/**
	 * 获取文件的父级目录
	 * @param rootPath 根目录
	 * @param curFile 当前的目录
	 * @return String 父级目录字符串
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static String getParentPath(String rootPath, File curFile) {
		if(curFile == null) {
			return "";
		}
		String parent = curFile.getParent();
		if(!StringUtils.hasText(rootPath)) {
			return parent;
		}
		File rootFile = new File(rootPath);
		String temp = parent.replace(rootFile.getPath(), "");
		if(!StringUtils.hasText(temp)) {
			return Symbols.FORWARD_SLASH;
		}
		return temp.replace(Symbols.BACK_SLASH, Symbols.FORWARD_SLASH);
	}
}
