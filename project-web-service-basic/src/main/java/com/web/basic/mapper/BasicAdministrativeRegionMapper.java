package com.web.basic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.basic.entity.BasicAdministrativeRegion;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 行政区划 省、市、区和街道 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-22
 */
@Mapper
public interface BasicAdministrativeRegionMapper extends BaseMapper<BasicAdministrativeRegion> {

	/**
	 * 获取所有省份
	 * @return List<BasicAdministrativeRegion> 省份数据列表
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	List<BasicAdministrativeRegion> getProvince();
	
	/**
	 * 根据省份获取市信息列表
	 * @return List<BasicAdministrativeRegion> 市信息列表
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	List<BasicAdministrativeRegion> getCityByProvince(@Param("region") BasicAdministrativeRegion region);
	
	/**
	 * 根据省、市获取县区列表
	 * @return List<BasicAdministrativeRegion> 市信息列表
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	List<BasicAdministrativeRegion> getCountryByCity(@Param("region") BasicAdministrativeRegion region);
}
