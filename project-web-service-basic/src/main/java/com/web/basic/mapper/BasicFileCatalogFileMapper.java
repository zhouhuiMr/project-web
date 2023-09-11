package com.web.basic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.basic.catalog.entity.BasicFileCatalogFile;
import com.web.common.basic.catalog.entity.BasicFileCatalogFileEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 文件目录对应的文件 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-16
 */
@Mapper
public interface BasicFileCatalogFileMapper extends BaseMapper<BasicFileCatalogFile> {

	/**
	 * 分页获取文件列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<BasicFileCatalogFile> 分页数据列表
	 *
	 * @author zhouhui
	 * @since 2023.03.19
	 */
	IPage<BasicFileCatalogFile> getCatalogFileList(Page<BasicFileCatalogFile> page, 
			@Param("condition") BasicFileCatalogFileEntity condition);
}
