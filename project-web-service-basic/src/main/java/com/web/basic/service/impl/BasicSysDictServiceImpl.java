package com.web.basic.service.impl;

import com.web.basic.mapper.BasicSysDictMapper;
import com.web.basic.service.BasicSysDictService;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.basic.entity.BasicSysDictEntity;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 系统的字典项配置  服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-30
 */
@Service
public class BasicSysDictServiceImpl extends ServiceImpl<BasicSysDictMapper, BasicSysDict> implements BasicSysDictService {

	@Override
	public R<List<BasicSysDict>> getDictListByType(List<String> typeList) {
		R<List<BasicSysDict>> json = new R<>();
		LambdaQueryWrapper<BasicSysDict> dictQuery = new LambdaQueryWrapper<>();
		dictQuery.eq(BasicSysDict::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		dictQuery.in(BasicSysDict::getType, typeList);
		List<BasicSysDict> dictList = this.baseMapper.selectList(dictQuery);
		if(dictList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
		}else {
			json.setResultEnum(ResultEnum.SUCCESS);
			json.setData(dictList);
		}
		return json;
	}

	@Override
	public R<IPage<BasicSysDict>> getDictList(BasicSysDictEntity condition) {
		R<IPage<BasicSysDict>> json = new R<>();
		setCondition(condition);
		IPage<BasicSysDict> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<BasicSysDict> result = baseMapper.getDictList(page, condition);
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
	 * @param condition 查询条件
	 *
	 * @author zhouhui
	 * @since 2023.01.02
	 */
	private void setCondition(BasicSysDictEntity condition) {
		if(StringUtils.hasText(condition.getType())) {
			condition.setType("%" + condition.getType() + "%");
		}
		if(StringUtils.hasText(condition.getLabel())) {
			condition.setLabel("%" + condition.getLabel() + "%");
		}
	}

	@Override
	public R<Object> saveDict(BasicSysDict dict) {
		R<Object> json = new R<>();
		//判断字典项是否存在
		LambdaQueryWrapper<BasicSysDict> query = new LambdaQueryWrapper<>();
		query.eq(BasicSysDict::getType, dict.getType());
		query.eq(BasicSysDict::getValue, dict.getValue());
		query.eq(BasicSysDict::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<BasicSysDict> dataList = baseMapper.selectList(query);
		if(dataList != null && !dataList.isEmpty()) {
			json.setResultEnum(ResultEnum.DATA_QUERY_SAME);
			return json;
		}
		WebUser user = UserUtil.getUser();
		dict.setChangeUserId(user.getUserId());
		dict.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
		dict.setCreateTime(LocalDateTime.now());
		
		baseMapper.insert(dict);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> updateDict(BasicSysDict dict) {
		R<Object> json = new R<>();
		if(dict.getMainId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择修改的字典项");
			return json;
		}
		WebUser user = UserUtil.getUser();
		LambdaUpdateWrapper<BasicSysDict> update = new LambdaUpdateWrapper<>();
		update.eq(BasicSysDict::getMainId, dict.getMainId());
		update.set(BasicSysDict::getLabel, dict.getLabel());
		update.set(BasicSysDict::getDescription, dict.getDescription());
		update.set(BasicSysDict::getSort, dict.getSort());
		update.set(BasicSysDict::getChangeUserId, user.getUserId());
		update.set(BasicSysDict::getChangeTime, LocalDateTime.now());
		int updateRow = baseMapper.update(null, update);
		if(updateRow == 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> deleteDict(BasicSysDict dict) {
		R<Object> json = new R<>();
		if(dict.getMainId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择要删除的字典项");
			return json;
		}
		WebUser user = UserUtil.getUser();
		LambdaUpdateWrapper<BasicSysDict> update = new LambdaUpdateWrapper<>();
		update.eq(BasicSysDict::getMainId, dict.getMainId());
		update.set(BasicSysDict::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		update.set(BasicSysDict::getChangeUserId, user.getUserId());
		update.set(BasicSysDict::getChangeTime, LocalDateTime.now());
		int delRow = baseMapper.update(null, update);
		if(delRow == 0) {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
			return json;
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

}
