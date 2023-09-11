package com.web.common.basic.catalog.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.catalog.entity.BasicFileCatalogEntity;
import com.web.common.basic.catalog.entity.BasicFileCatalogFile;
import com.web.common.basic.catalog.entity.BasicFileCatalogFileEntity;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "catalogApi", path = "/basic/catalog")
public interface CatalogApi {
	
	@PostMapping(path = "/downloadFile")
	ResponseEntity<byte[]> downloadFile(@RequestBody BasicFileCatalogFile file);

	/**
	 * 获取文件目录的列表
	 * @param condition 查询条件
	 * @return R<List<BasicFileCatalogEntity>> 目录列表
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	@PostMapping(value = "/getCatalogList")
	@ResponseBody
	R<List<BasicFileCatalogEntity>> getCatalogList(@RequestBody BasicFileCatalogEntity condition);
	
	/**
	 * 创建文件目录
	 * @param catalog 文件目录信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	@PostMapping(value = "/createCatalog")
	@ResponseBody
	R<Object> createCatalog(@Validated @RequestBody BasicFileCatalogEntity catalog);
	
	/**
	 * 删除文件目录
	 * @param catalog
	 * @return
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	@PostMapping(value = "/deleteCatalog")
	@ResponseBody
	R<Object> deleteCatalog(@RequestBody BasicFileCatalogEntity catalog);
	
	/**
	 * 获取目录下文件列表
	 * @param condition 查询条件
	 * @return R<IPage<BasicFileCatalogFile>> 分页信息列表
	 *
	 * @author zhouhui
	 * @since 2023.03.16
	 */
	@PostMapping(value = "/getCatalogFileList")
	@ResponseBody
	R<IPage<BasicFileCatalogFile>> getCatalogFileList(@RequestBody BasicFileCatalogFileEntity condition);

	/**
	 * 上传文件信息
	 * @param fileList 文件列表
	 * @param catalogPath 目录地址
	 * @return R<List<BasicFileCatalogFile>> 上传文件列表信息
	 *
	 * @author zhouhui
	 * @since 2023.03.18
	 */
	@PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	R<List<BasicFileCatalogFile>> uploadFile(@RequestPart("files") MultipartFile[] fileList, 
			@RequestParam("catalogPath") String catalogPath);
	
	/**
	 * 删除文件信息
	 * @param catalogFile 要删除的文件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.03.18
	 */
	@PostMapping(value = "/deleteFile")
	@ResponseBody
	R<Object> deleteFile(@RequestBody BasicFileCatalogFile catalogFile);
}
