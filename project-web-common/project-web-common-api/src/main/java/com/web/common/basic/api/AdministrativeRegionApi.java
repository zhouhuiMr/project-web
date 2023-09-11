package com.web.common.basic.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.entity.BasicAdministrativeRegion;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "administrativeRegionApi", path = "/basic/region")
public interface AdministrativeRegionApi {
	
	/**
	 * 获取所有省及省份编号
	 * @return ResultJson<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(path = "/getProvince")
	@ResponseBody
	R<List<BasicAdministrativeRegion>> getProvince();
	
	/**
	 * 根据省获取所有的市
	 * @param region 省的信息
	 * @return ResultJson<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(path = "/getCityByProvince")
	@ResponseBody
	R<List<BasicAdministrativeRegion>> getCityByProvince(@RequestBody BasicAdministrativeRegion region);
	
	/**
	 * 根据省市获取地区信息
	 * @param region 省市信息
	 * @return ResultJson<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(path = "/getcountryByCity")
	@ResponseBody
	R<List<BasicAdministrativeRegion>> getCountryByCity(@RequestBody BasicAdministrativeRegion region);
}
