package com.web.user.service.impl;

import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebDept;
import com.web.common.user.entity.WebDeptEntity;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import com.web.user.mapper.WebDeptMapper;
import com.web.user.service.WebDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门信息 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-05-08
 */
@Service
public class WebDeptServiceImpl extends ServiceImpl<WebDeptMapper, WebDept> implements WebDeptService {

	/** 部门树的根节点值 */
	private static final int DEPT_TREE_ROOT = 0;

	@Override
	public R<List<WebDeptEntity>> getDeptList(WebDeptEntity condition) {
		R<List<WebDeptEntity>> json = new R<>();
		List<WebDeptEntity> deptList = this.baseMapper.getDeptList(condition);
		if (deptList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		List<WebDeptEntity> deptTree = buildTree(deptList);
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(deptTree);
		return json;
	}

	/**
	 * 将部门列表构建成树状结构，因为已经按照父节点排序，
	 * 因此父节点在前。
	 * 
	 * @param deptList 部门列表
	 * @return List<WebDeptEntity> 部门列表
	 * 
	 * @author zhouhui
	 * @since 2023.05.14
	 */
	private List<WebDeptEntity> buildTree(List<WebDeptEntity> deptList) {
		List<WebDeptEntity> tree = new ArrayList<>(deptList.size());
		Map<Integer, WebDeptEntity> treeMap = new HashedMap<>(deptList.size());
		for (int i = 0; i < deptList.size(); i++) {
			WebDeptEntity item = deptList.get(i);
			if(item.getParentId() == null) {
				continue;
			}
			int parentId = item.getParentId().intValue();
			int deptId = item.getDeptId().intValue();
			if(parentId == DEPT_TREE_ROOT) {
				tree.add(item);
			}
			WebDeptEntity parentDept = treeMap.get(parentId);
			if(parentDept != null) {
				if(parentDept.getChildren() == null) {
					parentDept.setChildren(new ArrayList<>());
				}
				parentDept.getChildren().add(item);
			}
			treeMap.put(deptId, item);
		}
		return tree;
	}

	@Override
	public R<Object> saveDept(WebDept dept) {
		R<Object> json = new R<>();
		if(dept.getParentId().intValue() != DEPT_TREE_ROOT) {
			//判断父级部门是否存在
			getParentDept(dept.getParentId());
		}
		WebUser user = UserUtil.getUser();
		dept.setCreateUserId(user.getUserId());
		dept.setCreateTime(LocalDateTime.now());
		this.baseMapper.insert(dept);
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	

	@Override
	public R<Object> updateDept(WebDept dept) {
		R<Object> json = new R<>();
		if(dept.getParentId().intValue() != DEPT_TREE_ROOT) {
			//判断父级部门是否存在
			getParentDept(dept.getParentId());
		}
		WebUser user = UserUtil.getUser();
		dept.setUpdateUserId(user.getUserId());
		dept.setUpdateTime(LocalDateTime.now());
		LambdaUpdateWrapper<WebDept> update = new LambdaUpdateWrapper<>();
		update.eq(WebDept::getDeptId, dept.getDeptId());
		update.eq(WebDept::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		int row = this.baseMapper.update(dept, update);
		if(row <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> deleteDept(WebDept dept) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		
		LambdaQueryWrapper<WebDept> query = new LambdaQueryWrapper<>();
		query.eq(WebDept::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		query.eq(WebDept::getParentId, dept.getDeptId());
		List<WebDept> deptChildren = this.baseMapper.selectList(query);
		if(deptChildren != null && !deptChildren.isEmpty()) {
			throw new CustomException("存在子部门不允许删除");
		}
		LambdaUpdateWrapper<WebDept> update = new LambdaUpdateWrapper<>();
		update.eq(WebDept::getDeptId, dept.getDeptId());
		update.eq(WebDept::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(WebDept::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		update.set(WebDept::getUpdateUserId, user.getUserId());
		update.set(WebDept::getUpdateTime, LocalDateTime.now());
		this.baseMapper.update(null, update);
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 获取父级部门
	 * @param parentId 部门Id
	 * @return WebDept 部门信息
	 * 
	 * @author zhouhui
	 * @since 2023.05.14
	 */
	private WebDept getParentDept(Integer parentId) {
		LambdaQueryWrapper<WebDept> query = new LambdaQueryWrapper<>();
		query.eq(WebDept::getDeptId, parentId);
		query.eq(WebDept::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<WebDept> deptList = this.baseMapper.selectList(query);
		if(deptList == null || deptList.isEmpty()) {
			throw new CustomException("没有查询到父级部门");
		}
		return deptList.get(0);
	}
}
