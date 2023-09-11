package com.web.basic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.basic.catalog.entity.BasicFileCatalog;
import com.web.common.basic.catalog.entity.BasicFileCatalogEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 文件目录 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-04
 */
@Mapper
public interface BasicFileCatalogMapper extends BaseMapper<BasicFileCatalog> {
	
	/**
	 * 获取文件目录的列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return List<BasicFileCatalogEntity> 文件目录列表
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	List<BasicFileCatalogEntity> getCatalogList(@Param("condition") BasicFileCatalogEntity condition);
}
