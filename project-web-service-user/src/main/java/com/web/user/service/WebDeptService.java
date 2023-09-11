package com.web.user.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.WebDept;
import com.web.common.user.entity.WebDeptEntity;

/**
 * <p>
 * 部门信息 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2023-05-08
 */
public interface WebDeptService extends IService<WebDept> {

	/**
	 * 获取部门列表，组成树形结构进行返回，不进行分页。
	 * 注：不支持模糊查询
	 * @param condition 查询条件
	 * @return R<List<WebDeptEntity>> 层级关系部门列表
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	R<List<WebDeptEntity>> getDeptList(WebDeptEntity condition);
	
	/**
	 * 保存部门信息
	 * @param dept 部门信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	R<Object> saveDept(WebDept dept);
	
	/**
	 * 更新部门信息
	 * @param dept 部门信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	R<Object> updateDept(WebDept dept);
	
	/**
	 * 删除部门信息。
	 * 如果存在子级部门列表，不允许删除。
	 * @param dept 部门信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.05.07
	 */
	R<Object> deleteDept(WebDept dept);
}
