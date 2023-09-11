package com.web.common.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTransition {
	
	private FileTransition() {}
	
	/**
	 * 将文件转成byte数组
	 * @param path 文件所在路径
	 * @param delSourceFile 是否删除原文件，true删除；false不删除
	 * @return byte 转成byte数组（不为null）
	 *
	 * @author zhouhui
	 * @since 2022.02.05
	 */
	public static byte[] fileToByte(String path, boolean delSourceFile) {
		byte[] outByte = null;
		if(path == null || "".equals(path)) {
			return new byte[0];
		}
		File file = new File(path);
		if(!file.isFile()) {
			return new byte[0];
		}
		byte[] b = new byte[1024];
		try (
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				FileInputStream in = new FileInputStream(file);
				) {
			int len = 0;
			while((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			outByte = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(delSourceFile) {
			Path p = Paths.get(path);
			try {
				Files.deleteIfExists(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return outByte;
	}
}
