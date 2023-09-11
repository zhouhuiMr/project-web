package com.web.basic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.basic.service.BasicSysDictService;
import com.web.common.basic.api.BasicSysDictApi;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.basic.entity.BasicSysDictEntity;
import com.web.common.result.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 系统的字典项配置  前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-30
 */
@Tag(name = "字典项功能")
@Controller
@RequestMapping(path = "/basic/dict")
public class BasicSysDictController implements BasicSysDictApi{
	
	@Autowired
	private BasicSysDictService basicSysDictServiceImpl;

	@Operation(summary = "根据字典类型获取字典列表")
	@Override
	public R<List<BasicSysDict>> getDictListByType(List<String> typeList) {
		return basicSysDictServiceImpl.getDictListByType(typeList);
	}

	@Operation(summary = "获取字典项列表")
	@Override
	public R<IPage<BasicSysDict>> getDictList(BasicSysDictEntity condition) {
		return basicSysDictServiceImpl.getDictList(condition);
	}

	@Operation(summary = "保存字典项")
	@Override
	public R<Object> saveDict(BasicSysDict dict) {
		return basicSysDictServiceImpl.saveDict(dict);
	}

	@Operation(summary = "更新字典项")
	@Override
	public R<Object> updateDict(BasicSysDict dict) {
		return basicSysDictServiceImpl.updateDict(dict);
	}

	@Operation(summary = "删除字典项")
	@Override
	public R<Object> deleteDict(BasicSysDict dict) {
		return basicSysDictServiceImpl.deleteDict(dict);
	}

}
