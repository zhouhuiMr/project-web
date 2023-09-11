package com.web.basic.service.impl;

import com.web.basic.mapper.BasicFileCatalogMapper;
import com.web.basic.mapper.BasicSysDictMapper;
import com.web.common.basic.catalog.entity.BasicFileCatalog;
import com.web.common.basic.catalog.entity.BasicFileCatalogEntity;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.dictionary.DictionaryError;
import com.web.common.dictionary.VerifyDictionary;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.Symbols;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 文件目录 服务实现类
 * 
 * 主要用于管理文件信息，将文件放于自己的服务器，不使用OSS服务器，
 * OSS服务器下载需要花钱；比较好用的minio开源框架的使用AGPL开源协议，因此暂不考虑！！
 * 
 * 注：如果当前目录没有子目录或者文件，可以进行修改或者删除操作！！
 * 
 * 文件目录为树结构，
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-04
 */
@Service
public class BasicFileCatalogServiceImpl extends ServiceImpl<BasicFileCatalogMapper, BasicFileCatalog> {

	private static final String AUTH_TYPE_STR = "catalog_auth";

	/** 父节点为根目录 */
	private static final int ROOT_PARENT_ID = 0;

	@Value("${catalog.root: ''}")
	private String catalogRoot;

	@Autowired
	private BasicSysDictMapper basicSysDictMapper;

	/**
	 * 根据条件查询文件目录。<p>
	 * 循环两次，防止父目录的id大于子目录！！
	 * 
	 * @param condition 查询条件
	 * @return R<List<BasicFileCatalogEntity>> 文件目录列表
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	public R<List<BasicFileCatalogEntity>> getCatalogList(BasicFileCatalogEntity condition) {
		R<List<BasicFileCatalogEntity>> json = new R<>();
		List<BasicFileCatalogEntity> catalogList = this.baseMapper.getCatalogList(condition);
		if(catalogList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		
		List<BasicFileCatalogEntity> rootCatalogList = new ArrayList<>(catalogList.size());
		Map<Integer, BasicFileCatalogEntity> catalogMap = new HashedMap<>(catalogList.size());
		for(BasicFileCatalogEntity item : catalogList) {
			if(item.getParentId() == null) {
				continue;
			}
			if(item.getParentId().intValue() == 0) {
				rootCatalogList.add(item);
			}
			catalogMap.put(item.getCatalogId(), item);
		}
		
		for(BasicFileCatalogEntity item : catalogList) {
			Integer parentId = item.getParentId();
			BasicFileCatalogEntity catalog = catalogMap.get(parentId);
			if(catalog == null) {
				continue;
			}
			if(catalog.getChildren() == null) {
				catalog.setChildren(new ArrayList<>());
			}
			catalog.getChildren().add(item);
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(rootCatalogList);
		return json;
	}

	/**
	 * 创建文件目录。<br>
	 * 1、判断是否存在文件目录，不存在则进行创建目录；<br>
	 * 2、判断目录信息数据是否存在，如果存在更新数据；<br>
	 * 3、如果目录信息不存在则执行新增；<br>
	 * 4、判断目录的访问权限，如果部分有权限，保存目录与用户对应关系信息。<br>
	 * 
	 * @param condition 文件目录信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	public R<Object> createCatalog(BasicFileCatalogEntity catalog) {
		R<Object> json = new R<>();

		WebUser user = UserUtil.getUser();
		File rootFile = new File(catalogRoot);
		File file = new File(rootFile, catalog.getCatalogName());
		// 设置目录信息
		setCatalogPath(rootFile, file, catalog);
		// 设置父级目录
		setParentCatalog(rootFile, file, catalog);

		if (!file.exists()) {
			file.mkdirs();
		}
		boolean isRight = authTypeIsRight(catalog);
		if (!isRight) {
			json.setResultEnum(ResultEnum.DICTIONARY_VERIFY_ERROR);
			return json;
		}
		catalog.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
		catalog.setCreateUserId(user.getUserId());
		catalog.setCreateTime(LocalDateTime.now());
		catalog.setUpdateUserId(user.getUserId());
		catalog.setUpdateTime(LocalDateTime.now());

		// 2、获取文件目录列表
		LambdaQueryWrapper<BasicFileCatalog> query = new LambdaQueryWrapper<>();
		query.eq(BasicFileCatalog::getCatalogName, catalog.getCatalogName());
		query.eq(BasicFileCatalog::getCatalogPath, catalog.getCatalogPath());
		List<BasicFileCatalog> catalogList = this.baseMapper.selectList(query);
		if (catalogList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		if (catalogList.isEmpty()) {
			this.baseMapper.insert(catalog);
		} else {
			LambdaUpdateWrapper<BasicFileCatalog> updateCatalog = new LambdaUpdateWrapper<>();
			updateCatalog.eq(BasicFileCatalog::getCatalogName, catalog.getCatalogName());
			this.baseMapper.update(catalog, updateCatalog);
		}

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 验证文件目录信息是否符合要求。<p> 
	 * 注：目录名称可能直接设置“/”或者直接没有“/”开头！
	 * 
	 * @param rootFile 根目录
	 * @param curFile  当前文件
	 * @param catalog  文件目录
	 *
	 * @author zhouhui
	 * @since 2023.03.09
	 */
	private void setCatalogPath(File rootFile, File curFile, BasicFileCatalogEntity catalog) {
		String catalogName = curFile.getName();
		if (!StringUtils.hasText(catalogName)) {
			throw new CustomException("请设置目录名称");
		}
		catalog.setCatalogName(catalogName);
		String path = curFile.getPath().replace(rootFile.getPath(), "");
		catalog.setCatalogPath(path.replace(Symbols.BACK_SLASH, Symbols.FORWARD_SLASH));
	}

	/**
	 * 获取当前目录的父级目录ID。
	 * @param rootFile 根目录
	 * @param curFile 当前的路径
	 * @param catalog 目录信息
	 *
	 * @author zhouhui
	 * @since 2023.03.08
	 */
	private void setParentCatalog(File rootFile, File curFile, BasicFileCatalogEntity catalog) {
		File parentFile = curFile.getParentFile();
		if (rootFile.getPath().equals(parentFile.getPath())) {
			catalog.setParentId(ROOT_PARENT_ID);
			return;
		}
		
		String temp = parentFile.getPath().replace(rootFile.getPath(), "");
		String parentPath = temp.replace(Symbols.BACK_SLASH, Symbols.FORWARD_SLASH);

		LambdaQueryWrapper<BasicFileCatalog> query = new LambdaQueryWrapper<>();
		query.eq(BasicFileCatalog::getCatalogPath, parentPath);
		query.eq(BasicFileCatalog::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<BasicFileCatalog> parentList = this.baseMapper.selectList(query);
		if (parentList == null || parentList.isEmpty()) {
			throw new CustomException("没有查询到父级目录");
		}
		catalog.setParentId(parentList.get(0).getCatalogId());
	}

	/**
	 * 判断授权类型是否存在
	 * 
	 * @param catalog 目录信息
	 * @return boolean true正确；false不正确
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	private boolean authTypeIsRight(BasicFileCatalog catalog) {
		boolean isExist = false;
		LambdaQueryWrapper<BasicSysDict> dictQuery = new LambdaQueryWrapper<>();
		dictQuery.eq(BasicSysDict::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		dictQuery.eq(BasicSysDict::getType, AUTH_TYPE_STR);
		List<BasicSysDict> dictList = basicSysDictMapper.selectList(dictQuery);
		List<DictionaryError> errorList = VerifyDictionary.operateDict(catalog, dictList);
		if (errorList != null && errorList.isEmpty()) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * 删除文件目录。<p>
	 * 注： 1、只有文件目录下没有子目录或者文件的情况才允许删除！！<br>
	 * 2、可能存在数据库数据和文件目录不对应的问题！！<br>
	 * 
	 * @param catalog 目录信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.03.11
	 */
	@Transactional
	public R<Object> deleteCatalog(BasicFileCatalogEntity catalog) {
		R<Object> json = new R<>();
		if (catalog.getCatalogId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择要删除的目录");
			return json;
		}
		BasicFileCatalog catalogData = this.baseMapper.selectById(catalog.getCatalogId());
		if (catalogData == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_NO_RESULT.getMessage());
		}
		File file = new File(catalogRoot, catalogData.getCatalogPath());
		if (file.exists()) {
			String[] files = file.list();
			if (files.length > 0) {
				throw new CustomException("当前目录下存在目录或者文件不能删除");
			}
		}
		// 目录不存在直接删除数据库
		this.baseMapper.deleteById(catalog.getCatalogId());
		if (file.exists()) {
			try {
				Files.delete(Paths.get(file.getPath()));
			} catch (IOException e) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				e.printStackTrace();
			}
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 更新文件目录。<p>
	 * 注：1、修改的目录只有在不存在子目录和文件的情况下才能进行修改；
	 * 2、只允许修改文件的名称。
	 * @param catalog 文件目录
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.03.11
	 */
	public R<Object> updateCatalog(BasicFileCatalogEntity catalog) {
		R<Object> json = new R<>();
		if (catalog.getCatalogId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择要删除的目录");
			return json;
		}
		BasicFileCatalog catalogData = this.baseMapper.selectById(catalog.getCatalogId());
		if (catalogData == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_NO_RESULT.getMessage());
		}
		return json;
	}
}
