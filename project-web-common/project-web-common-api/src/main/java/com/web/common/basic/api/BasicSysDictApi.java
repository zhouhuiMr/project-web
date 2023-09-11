package com.web.common.basic.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.basic.entity.BasicSysDictEntity;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "sysDictApi", path = "/basic/dict")
public interface BasicSysDictApi {

	/**
	 * 根据字典项类型的列表获取字典项列表
	 * @param typeList 类型列表
	 * @return List<BasicSysDict> 字典列表
	 *
	 * @author zhouhui
	 * @since 2022.11.30
	 */
	@PostMapping(path = "/listByType")
	@ResponseBody
	R<List<BasicSysDict>> getDictListByType(@RequestBody List<String> typeList);
	
	/**
	 * 获取字典列表
	 * @param condition 查询条件
	 * @return IPage<BasicSysDict> 分页列表
	 * 
	 * @author zhouhui
	 * @since 2023.01.02
	 */
	@PostMapping(path = "/getDictList")
	@ResponseBody
	R<IPage<BasicSysDict>> getDictList(@RequestBody BasicSysDictEntity condition);
	
	/**
	 * 保存字典项
	 * @param dict 保存的信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.01.02
	 */
	@PostMapping(path = "/saveDict")
	@ResponseBody
	R<Object> saveDict(@Valid @RequestBody BasicSysDict dict);
	
	/**
	 * 更新字典项
	 * @param dict 更新的信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.01.02
	 */
	@PostMapping(path = "/updateDict")
	@ResponseBody
	R<Object> updateDict(@Valid @RequestBody BasicSysDict dict);
	
	/**
	 * 删除字典项
	 * @param dict 需要删除的字典项的ID
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.01.02
	 */
	@PostMapping(path = "/deleteDict")
	@ResponseBody
	R<Object> deleteDict(@RequestBody BasicSysDict dict);
}
