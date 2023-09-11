package com.web.basic.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.basic.service.impl.BasicFileCatalogFileServiceImpl;
import com.web.basic.service.impl.BasicFileCatalogServiceImpl;
import com.web.common.basic.catalog.api.CatalogApi;
import com.web.common.basic.catalog.entity.BasicFileCatalogEntity;
import com.web.common.basic.catalog.entity.BasicFileCatalogFile;
import com.web.common.basic.catalog.entity.BasicFileCatalogFileEntity;
import com.web.common.result.R;
import com.web.service.filter.resubmit.Resubmit;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 文件目录 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-04
 */
@Tag(name = "文件目录")
@Controller
@RequestMapping("/basic/catalog")
public class BasicFileCatalogController implements CatalogApi{
	
	@Autowired
	private BasicFileCatalogServiceImpl basicFileCatalogServiceImpl;
	
	@Autowired
	private BasicFileCatalogFileServiceImpl basicFileCatalogFileServiceImpl;
	
	@Operation(summary = "获取文件信息")
	@Override
	public ResponseEntity<byte[]> downloadFile(BasicFileCatalogFile file) {
		String fileName = file.getFileName();
		return basicFileCatalogFileServiceImpl.downloadFile(fileName);
	}

	@Operation(summary = "获取文件目录列表")
	@Override
	public R<List<BasicFileCatalogEntity>> getCatalogList(BasicFileCatalogEntity condition) {
		return basicFileCatalogServiceImpl.getCatalogList(condition);
	}

	@Resubmit
	@Operation(summary = "创建文件目录")
	@Override
	public R<Object> createCatalog(BasicFileCatalogEntity catalog) {
		return basicFileCatalogServiceImpl.createCatalog(catalog);
	}

	@Resubmit
	@Operation(summary = "删除文件目录")
	@Override
	public R<Object> deleteCatalog(BasicFileCatalogEntity catalog) {
		return basicFileCatalogServiceImpl.deleteCatalog(catalog);
	}

	@Operation(summary = "获取文件列表")
	@Override
	public R<IPage<BasicFileCatalogFile>> getCatalogFileList(BasicFileCatalogFileEntity condition) {
		return basicFileCatalogFileServiceImpl.getCatalogFileList(condition);
	}

	@Resubmit
	@Operation(summary = "上传文件")
	@Override
	public R<List<BasicFileCatalogFile>> uploadFile(MultipartFile[] fileList, String catalogPath) {
		return basicFileCatalogFileServiceImpl.uploadFile(fileList, catalogPath);
	}

	@Resubmit
	@Operation(summary = "删除文件")
	@Override
	public R<Object> deleteFile(BasicFileCatalogFile catalogFile) {
		return basicFileCatalogFileServiceImpl.deleteFile(catalogFile);
	}
}
