package com.web.basic.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.basic.entity.BasicAdministrativeRegion;
import com.web.common.result.R;

/**
 * <p>
 * 行政区划 省、市、区和街道 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-22
 */
public interface BasicAdministrativeRegionService extends IService<BasicAdministrativeRegion> {

	/**
	 * 获取所有省份
	 * @return List<BasicAdministrativeRegion> 省份列表
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<List<BasicAdministrativeRegion>> getProvince();
	
	/**
	 * 根据省份获取对应市
	 * @param region 省份信息
	 * @return List<BasicAdministrativeRegion> 省份列表
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<List<BasicAdministrativeRegion>> getCityByProvince(BasicAdministrativeRegion region);
	
	/**
	 * 根据省、市获取对应的地址信息
	 * @param region 省、市份信息
	 * @return List<BasicAdministrativeRegion> 地区列表
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	R<List<BasicAdministrativeRegion>> getCountryByCity(BasicAdministrativeRegion region);
}
