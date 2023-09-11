package com.web.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.util.FileSystemUtils;

public class FileToZip {

	private FileToZip() {
	}

	/**
	 * 将文件或者文件夹转成zip文件，并保持文件夹的结构
	 * 
	 * @param target        zip文件
	 * @param source        原文件或者原文件夹
	 * @param delSourceFile 是否删除原文件，true删除；false不删除
	 * @return boolean true执行成功；false执行失败
	 *
	 * @author zhouhui
	 * @since 2022.02.04
	 */
	public static boolean fileToZip(String target, String source, boolean delSourceFile) {
		try (
				ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target));
				) {
			File file = new File(source);
			if (file.isFile()) {
				compress(zip, file, "");
			} else {
				compress(zip, file, file.getName());
			}
			// 删除文件或者文件夹
			if (delSourceFile) {
				if (file.isFile()) {
					Path path = Paths.get(source);
					Files.delete(path);
				} else {
					FileSystemUtils.deleteRecursively(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 压缩文件
	 * @param zip
	 * @param file
	 * @param pathName
	 * @throws IOException
	 *
	 * @author zhouhui
	 * @since 2022.12.21
	 */
	public static void compress(ZipOutputStream zip, File file, String pathName) throws IOException {
		if (file.isFile()) {
			// 文件
			byte[] bf = new byte[1024];
			try (
					FileInputStream in = new FileInputStream(file);
					) {
				String zipPath = "";
				if (pathName != null && !"".equals(pathName)) {
					zipPath = (pathName + "/" + file.getName());
				} else {
					zipPath = file.getName();
				}
				zip.putNextEntry(new ZipEntry(zipPath));
				
				int len = 0;
				while ((len = in.read(bf)) != -1) {
					zip.write(bf, 0, len);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (zip != null) {
					zip.closeEntry();
				}
			}
		} else {
			// 文件夹
			for (File fileItem : file.listFiles()) {
				String path = "";
				if (fileItem.isFile()) {
					path = pathName;
				} else {
					path = pathName + "/" + fileItem.getName();
				}
				compress(zip, fileItem, path);
			}
		}
	}
}
