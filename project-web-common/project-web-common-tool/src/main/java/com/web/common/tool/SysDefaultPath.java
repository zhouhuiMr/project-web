package com.web.common.tool;

import java.io.File;
import java.net.URL;

import org.springframework.util.ClassUtils;

public class SysDefaultPath {
	
	private SysDefaultPath() {}

	public static void setProjectPath() {
		String classPathFile = null;
		String projectPath = "";
		ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
		if(classLoader == null) {
			return;
		}
		URL resource = classLoader.getResource("");
		if(resource == null) {
			return;
		}
		classPathFile = classLoader.getResource("").getPath();
		File classpath = new File(classPathFile);
		projectPath = classpath.getParentFile().getParentFile().getParentFile().getPath();
		projectPath = projectPath.replaceFirst("file:", "");
		System.setProperty("projectpath", projectPath);
	}
}
