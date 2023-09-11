package com.web.basic.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.basic.entity.BasicSysDictEntity;
import com.web.common.result.R;

/**
 * <p>
 * 系统的字典项配置  服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-30
 */
public interface BasicSysDictService extends IService<BasicSysDict> {
	
	/**
	 * 根据字典项类型的列表获取字典项列表
	 * @param typeList 字典项类型列表
	 * @return List<BasicSysDict> 字典项列表
	 *
	 * @author zhouhui
	 * @since 2022.11.30
	 */
	R<List<BasicSysDict>> getDictListByType(List<String> typeList);
	
	/**
	 * 分页获取字典项列表
	 * @param condition 查询条件
	 * @return IPage<BasicSysDict> 分页列表
	 * 
	 * @author zhouhui
	 * @since 2022.01.02
	 */
	R<IPage<BasicSysDict>> getDictList(BasicSysDictEntity condition);
	
	/**
	 * 保存字典项
	 * @param dict 字典项信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2022.01.02
	 */
	R<Object> saveDict(BasicSysDict dict);
	
	/**
	 * 更新字典项
	 * @param dict 字典项信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2022.01.02
	 */
	R<Object> updateDict(BasicSysDict dict);
	
	/**
	 * 删除字典项
	 * @param dict 字典项ID
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2022.01.02
	 */
	R<Object> deleteDict(BasicSysDict dict);
}
