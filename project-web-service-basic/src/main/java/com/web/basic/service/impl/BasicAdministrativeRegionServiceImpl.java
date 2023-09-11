package com.web.basic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.basic.mapper.BasicAdministrativeRegionMapper;
import com.web.basic.service.BasicAdministrativeRegionService;
import com.web.common.basic.entity.BasicAdministrativeRegion;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 行政区划 省、市、区和街道 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 1.0.0
 */
@Service
public class BasicAdministrativeRegionServiceImpl extends ServiceImpl<BasicAdministrativeRegionMapper, BasicAdministrativeRegion> implements BasicAdministrativeRegionService {

	@Override
	public R<List<BasicAdministrativeRegion>> getProvince() {
		R<List<BasicAdministrativeRegion>> json = new R<>();
		List<BasicAdministrativeRegion> dataList = baseMapper.getProvince();
		if(dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}

	@Override
	public R<List<BasicAdministrativeRegion>> getCityByProvince(BasicAdministrativeRegion region) {
		R<List<BasicAdministrativeRegion>> json = new R<>();
		
		List<BasicAdministrativeRegion> dataList = baseMapper.getCityByProvince(region);
		if(dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}

	@Override
	public R<List<BasicAdministrativeRegion>> getCountryByCity(BasicAdministrativeRegion region) {
		R<List<BasicAdministrativeRegion>> json = new R<>();
		
		List<BasicAdministrativeRegion> dataList = baseMapper.getCountryByCity(region);
		if(dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}

}
