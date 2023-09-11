package com.web.basic.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.basic.service.BasicAdministrativeRegionService;
import com.web.common.basic.api.AdministrativeRegionApi;
import com.web.common.basic.entity.BasicAdministrativeRegion;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 行政区划 省、市、区和街道 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2022-12-22
 */
@Tag(name = "行政区划 省、市、区和街道")
@Controller
@RequestMapping(path = "/basic/region")
public class BasicAdministrativeRegionController implements AdministrativeRegionApi{

	@Autowired
	private BasicAdministrativeRegionService administrativeRegion;
	
	@Operation(summary = "获取所有省份")
	@Override
	public R<List<BasicAdministrativeRegion>> getProvince(){
		return administrativeRegion.getProvince();
	}
	
	@Operation(summary = "根据省获取所有市")
	@Override
	public R<List<BasicAdministrativeRegion>> getCityByProvince(@RequestBody BasicAdministrativeRegion region){
		R<List<BasicAdministrativeRegion>> json = new R<>();
		if(!StringUtils.hasText(region.getProvName()) && !StringUtils.hasText(region.getProvCode())) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		json = administrativeRegion.getCityByProvince(region);
		return json;
	}
	
	@Operation(summary = "根据省、市获取所有地区")
	@Override
	public R<List<BasicAdministrativeRegion>> getCountryByCity(@RequestBody BasicAdministrativeRegion region){
		R<List<BasicAdministrativeRegion>> json = new R<>();
		
		if(!StringUtils.hasText(region.getProvName()) && !StringUtils.hasText(region.getProvCode())) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		if(!StringUtils.hasText(region.getCityName()) && !StringUtils.hasText(region.getCityCode())) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		json = administrativeRegion.getCountryByCity(region);
		return json;
	}
}
