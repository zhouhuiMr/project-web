package com.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.SysDefaultAdmin;
import com.web.common.user.entity.WebRole;
import com.web.common.user.entity.WebRoleEntity;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.user.mapper.WebRoleMapper;
import com.web.user.service.WebRoleService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户的角色 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Service
public class WebRoleServiceImpl extends ServiceImpl<WebRoleMapper, WebRole> implements WebRoleService {
	
	@Autowired
	private WebRoleMapper webRoleMapper;

	@Override
	public R<IPage<WebRoleEntity>> getRoleList(WebRoleEntity condition) {
		R<IPage<WebRoleEntity>> json = new R<>();
		setSearchCondition(condition);
		
		Page<WebRole> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<WebRoleEntity> dataList = webRoleMapper.getRoleList(page, condition);
		if(dataList == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("获取角色信息失败");
		}
		json.setData(dataList);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	/**
	 * 设置查询信息
	 * @param condition 查询条件
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	private void setSearchCondition(WebRoleEntity condition) {
		if(StringUtils.hasText(condition.getRoleName())) {
			condition.setRoleName("%" + condition.getRoleName() + "%");
		}
	}

	@Override
	public R<WebRoleEntity> saveRole(WebRoleEntity webRole) {
		R<WebRoleEntity> json = new R<>();
		WebUser user = UserUtil.getUser();
		webRole.setCreateUserId(user.getUserId());
		webRole.setUpdateUserId(user.getUserId());
		webRole.setCreateTime(LocalDateTime.now());
		webRole.setUpdateTime(LocalDateTime.now());
		
		WebRole role = new WebRole();
		BeanUtils.copyProperties(webRole, role);
		int insertRow = webRoleMapper.insert(role);
		if(insertRow == 0) {
			json.setResultEnum(ResultEnum.DATA_INSERT_ERROR);
			return json;
		}
		webRole.setRoleId(role.getRoleId());
		json.setData(webRole);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<WebRoleEntity> updateRole(WebRoleEntity webRole) {
		R<WebRoleEntity> json = new R<>();
		if(!roleIsExistById(webRole)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("没有查询到对应的角色");
			return json;
		}
		WebUser user = UserUtil.getUser();
		webRole.setUpdateUserId(user.getUserId());
		webRole.setUpdateTime(LocalDateTime.now());
		
		WebRole role = new WebRole();
		BeanUtils.copyProperties(webRole, role);
		
		LambdaUpdateWrapper<WebRole> update = new LambdaUpdateWrapper<>();
		update.eq(WebRole::getRoleId, webRole.getRoleId());
		update.eq(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		int updateRow = webRoleMapper.update(role, update);
		if(updateRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> deleteRole(WebRoleEntity webRole) {
		R<Object> json = new R<>();
		if(!roleIsExistById(webRole)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("没有查询到对应的角色");
			return json;
		}
		if(webRole.getRoleId().intValue() == SysDefaultAdmin.SYSTEM_DEFAULT_ROLE) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("默认系统管理员不能删除");
			return json;
		}
		WebUser user = UserUtil.getUser();
		
		LambdaUpdateWrapper<WebRole> update = new LambdaUpdateWrapper<>();
		update.eq(WebRole::getRoleId, webRole.getRoleId());
		update.eq(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		update.set(WebRole::getUpdateUserId, user.getUserId());
		update.set(WebRole::getUpdateTime, LocalDateTime.now());
		int updateRow = webRoleMapper.update(null, update);
		if(updateRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 判断角色信息是否存在
	 * @param webRole 角色信息
	 * @return boolean true存在；false不存在
	 *
	 * @author zhouhui
	 * @since 2022-09-13
	 */
	private boolean roleIsExistById(WebRoleEntity webRole) {
		LambdaQueryWrapper<WebRole> query = new LambdaQueryWrapper<>();
		query.eq(WebRole::getRoleId, webRole.getRoleId());
		query.eq(WebRole::getDelFlag, 0);
		List<WebRole> roleList = webRoleMapper.selectList(query);
		return !(roleList == null || roleList.isEmpty());
	}
	
}
