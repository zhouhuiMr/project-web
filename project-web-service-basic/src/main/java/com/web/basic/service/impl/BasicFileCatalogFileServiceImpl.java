package com.web.basic.service.impl;

import com.web.basic.mapper.BasicFileCatalogFileMapper;
import com.web.basic.mapper.BasicFileCatalogMapper;
import com.web.common.basic.catalog.entity.BasicFileCatalog;
import com.web.common.basic.catalog.entity.BasicFileCatalogFile;
import com.web.common.basic.catalog.entity.BasicFileCatalogFileEntity;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.Symbols;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件目录对应的文件 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-16
 */
@Service
public class BasicFileCatalogFileServiceImpl extends ServiceImpl<BasicFileCatalogFileMapper, BasicFileCatalogFile> {

	@Value("${catalog.root: ''}")
	private String catalogRoot;
	
	@Value("${catalog.fileType}")
	private List<String> canUploadFileType;

	@Autowired
	private BasicFileCatalogMapper basicFileCatalogMapper;

	private Random random = new Random();
	
	/**
	 * 获取下载的文件，将文件转成byte[]
	 * @param fileName 文件名称
	 * @return byte[]
	 *
	 * @author zhouhui
	 * @since 2023.03.26
	 */
	public ResponseEntity<byte[]> downloadFile(String fileName) {
		if(!StringUtils.hasText(fileName)) {
			throw new CustomException("请先选择要下载的文件");
		}
		LambdaQueryWrapper<BasicFileCatalogFile> query = new LambdaQueryWrapper<>();
		query.eq(BasicFileCatalogFile::getFileName, fileName);
		query.eq(BasicFileCatalogFile::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<BasicFileCatalogFile> fileList = this.baseMapper.selectList(query);
		if(fileList == null || fileList.isEmpty()) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		//获取磁盘的文件
		BasicFileCatalogFile file = fileList.get(0);
		String catalogpath = file.getCatalogPath();
		String path = catalogRoot + Symbols.FORWARD_SLASH + catalogpath + Symbols.FORWARD_SLASH + fileName;
		
		byte[] fileByte = null;
		try {
			fileByte = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(fileByte == null) {
			throw new CustomException("文件读取失败");
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		try {
			headers.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getOrigFileName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		List<String> exposeHeaders = new ArrayList<>();
		exposeHeaders.add("Content-Disposition");
		headers.setAccessControlExposeHeaders(exposeHeaders);
		return new ResponseEntity<>(fileByte, headers, HttpStatus.OK);
	}
	
	/**
	 * 根据目录获取文件列表
	 * @param condition 插叙条件
	 * @return R<IPage<BasicFileCatalogFile>> 分页数据列表
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	public R<IPage<BasicFileCatalogFile>> getCatalogFileList(BasicFileCatalogFileEntity condition) {
		R<IPage<BasicFileCatalogFile>> json = new R<>();
		if(condition.getCatalogId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择文件目录");
			return json;
		}
		setSearchCondition(condition);
		Page<BasicFileCatalogFile> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<BasicFileCatalogFile> result = this.baseMapper.getCatalogFileList(page, condition);
		if(result == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(result);
		return json;
	}
	
	/**
	 * 设置查询条件
	 * @param condition 查询信息
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	private void setSearchCondition(BasicFileCatalogFileEntity condition) {
		if(StringUtils.hasText(condition.getFileName())) {
			condition.setFileName("%" + condition.getFileName() + "%");
		}
		if(StringUtils.hasText(condition.getOrigFileName())) {
			condition.setOrigFileName("%" + condition.getOrigFileName() + "%");
		}
	}

	/**
	 * 上传文件列表。 
	 * 1、判断文件目录是否存在；<br> 
	 * 2、将文件写入数据库；<br> 
	 * 3、将数据写到目录。<br> 
	 * 注：可能存在数据写入磁盘，但没写入数据库！！
	 * 
	 * @param fileList
	 * @return
	 *
	 * @author zhouhui
	 * @since 2023.03.18
	 */
	@Transactional
	public R<List<BasicFileCatalogFile>> uploadFile(MultipartFile[] fileList, String catalogPath) {
		R<List<BasicFileCatalogFile>> json = new R<>();

		WebUser user = UserUtil.getUser();

		BasicFileCatalog catalog = checkUploadFile(fileList, catalogPath);

		String path = catalogRoot + catalogPath;

		// 将文件信息写入数据库
		List<BasicFileCatalogFile> saveFiles = new ArrayList<>(fileList.length);
		for (MultipartFile item : fileList) {
			BasicFileCatalogFile file = new BasicFileCatalogFile();
			file.setOrigFileName(item.getOriginalFilename());
			file.setCatalogId(catalog.getCatalogId());
			file.setCatalogPath(catalog.getCatalogPath());

			String fileType = getFileType(item.getOriginalFilename());
			file.setFileType(fileType);
			file.setFileName(createFileName() + Symbols.DOT + fileType);
			file.setCreateTime(LocalDateTime.now());
			file.setUpdateTime(LocalDateTime.now());
			if (user != null) {
				file.setCreateUserId(user.getUserId());
				file.setUpdateUserId(user.getUserId());
			}
			saveFiles.add(file);
			
			// 写入磁盘
			String filePath = path + Symbols.FORWARD_SLASH + file.getFileName();
			try (FileChannel outChannel = new FileOutputStream(filePath).getChannel();
					InputStream in = item.getInputStream();
					ReadableByteChannel inChannel = Channels.newChannel(in);) {
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				while (inChannel.read(buffer) != -1) {
					buffer.flip();
					outChannel.write(buffer);
					buffer.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.saveBatch(saveFiles);

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 判断文件目录是否存在
	 * 
	 * @param fileList    文件列表
	 * @param catalogPath 文件目录
	 * @return BasicFileCatalog 文件目录信息
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	private BasicFileCatalog checkUploadFile(MultipartFile[] fileList, String catalogPath) {
		if (fileList == null || fileList.length <= 0) {
			throw new CustomException(ResultEnum.NO_FILE_UPLOAD.getMessage());
		}
		for (MultipartFile file : fileList) {
			if (file.isEmpty()) {
				throw new CustomException(ResultEnum.NO_FILE_UPLOAD.getMessage());
			}
		}
		if (!StringUtils.hasText(catalogPath)) {
			throw new CustomException(ResultEnum.FILE_UPLOAD_PATH_EXIST.getMessage());
		}

		String path = catalogRoot + catalogPath;

		if (!Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS)) {
			throw new CustomException(ResultEnum.FILE_UPLOAD_PATH_EXIST.getMessage());
		}

		LambdaQueryWrapper<BasicFileCatalog> catalog = new LambdaQueryWrapper<>();
		catalog.eq(BasicFileCatalog::getCatalogPath, catalogPath);
		catalog.eq(BasicFileCatalog::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<BasicFileCatalog> catalogList = basicFileCatalogMapper.selectList(catalog);
		if (catalogList == null || catalogList.isEmpty()) {
			throw new CustomException(ResultEnum.FILE_UPLOAD_PATH_EXIST.getMessage());
		}

		return catalogList.get(0);
	}

	/**
	 * 生成文件名称 时间戳+5位随机数
	 * 
	 * @return 文件名称
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	private String createFileName() {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String randomStr = random.nextInt(99999) + "";
		return dateTime + randomStr;
	}

	/**
	 * 根据文件名称获取文件类型
	 * 
	 * @param fileName 文件名称
	 * @return 文件类型
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	private String getFileType(String fileName) {
		if (!StringUtils.hasText(fileName)) {
			throw new CustomException("没有文件名称");
		}
		String[] types = fileName.split(Symbols.DOT_ESCAPE);
		if (types == null || types.length <= 1) {
			throw new CustomException("未查询到文件类型");
		}
		String fileType = types[types.length - 1].toLowerCase();
		if(!canUploadFileType.contains(fileType)) {
			throw new CustomException("禁止上传此文件类型");
		}
		return fileType;
	}
	
	/**
	 * 删除文件信息，
	 * 1、判断数据库中是否存在；
	 * 2、如果数据库存在，删除数据库中数据；
	 * 3、判断磁盘中是否存在，如果存在则进行删除。
	 * @param catalogFile 文件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	public R<Object> deleteFile(BasicFileCatalogFile catalogFile) {
		R<Object> json = new R<>();
		if(catalogFile.getFileId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		BasicFileCatalogFile file = this.baseMapper.selectById(catalogFile.getFileId());
		if(file == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_NO_RESULT);
			return json;
		}
		//删除文件数据
		this.baseMapper.deleteById(file.getFileId());
		//删除磁盘文件
		String filePath = catalogRoot + file.getCatalogPath() + Symbols.FORWARD_SLASH + file.getFileName();
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
}
