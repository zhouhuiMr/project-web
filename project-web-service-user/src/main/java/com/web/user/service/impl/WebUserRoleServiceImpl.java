package com.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.security.AuthenticationEntity;
import com.web.common.user.entity.WebRole;
import com.web.common.user.entity.WebUser;
import com.web.common.user.entity.WebUserRole;
import com.web.common.user.entity.WebUserRoleEntity;
import com.web.service.handler.auth.UserUtil;
import com.web.user.mapper.WebRoleMapper;
import com.web.user.mapper.WebUserRoleMapper;
import com.web.user.service.WebUserRoleService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户对应的角色关系 一个用户可以对应多个角色 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Service
public class WebUserRoleServiceImpl extends ServiceImpl<WebUserRoleMapper, WebUserRole> implements WebUserRoleService {
	
	/** 系统管理员对应的用户ID */
	public static final Integer MANAGER_USER_ID = 1;
	
	@Autowired
	private WebRoleMapper webRoleMapper;
	
	@Autowired
	private WebUserRoleMapper webUserRoleMapper;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public R<IPage<WebUserRoleEntity>> getUserRoleList(WebUserRoleEntity condition) {
		R<IPage<WebUserRoleEntity>> json = new R<>();
		if(StringUtils.hasText(condition.getRoleName())) {
			condition.setRoleName("%" + condition.getRoleName() + "%");
		}
		Page<WebUserRole> page = new Page<>(condition.getPage(),condition.getSize());
		IPage<WebUserRoleEntity> result = webUserRoleMapper.getUserRoleList(page, condition);
		if(result == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		List<WebUserRoleEntity> dataList = result.getRecords();
		for(WebUserRoleEntity item : dataList) {
			Integer userId = item.getUserId();
			if(userId == null) {
				item.setAuthorityStatus(0);
			}else {
				item.setAuthorityStatus(1);
			}
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(result);
		return json;
	}

	@Override
	@Transactional
	public R<Object> updateUserRole(WebUserRoleEntity userRole) {
		R<Object> json = new R<>();
		
		WebUser loginUser = UserUtil.getUser();
		
		Integer userId = userRole.getUserId();
		if(userId.equals(MANAGER_USER_ID)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("不能设置系统管理员的角色");
			return json;
		}
		if(userRole.getAuthorityStatus() == null || (!DataSourcesSymbol.USER_ROLE_STATUS_0.equals(userRole.getAuthorityStatus()) 
				&& !DataSourcesSymbol.USER_ROLE_STATUS_1.equals(userRole.getAuthorityStatus())) ) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请设置授权状态");
			return json;
		}
		//判断角色是否存在
		LambdaQueryWrapper<WebRole> roleQuery = new LambdaQueryWrapper<>();
		roleQuery.eq(WebRole::getRoleId, userRole.getRoleId());
		roleQuery.eq(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<WebRole> roleList = webRoleMapper.selectList(roleQuery);
		if(roleList == null || roleList.isEmpty()) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("没有查询到对应的角色信息");
			return json;
		}
		
		LambdaUpdateWrapper<WebUserRole> update = new LambdaUpdateWrapper<>();
		update.eq(WebUserRole::getUserId, userRole.getUserId());
		update.eq(WebUserRole::getRoleId, userRole.getRoleId());
		update.eq(WebUserRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		if(DataSourcesSymbol.USER_ROLE_STATUS_0.equals(userRole.getAuthorityStatus())) {
			update.set(WebUserRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		}
		update.set(WebUserRole::getUpdateUserId, loginUser.getUserId());
		update.set(WebUserRole::getUpdateTime, LocalDateTime.now());
		int updateRow = webUserRoleMapper.update(null, update);
		if(DataSourcesSymbol.USER_ROLE_STATUS_1.equals(userRole.getAuthorityStatus()) && updateRow == 0) {
			//新增授权信息
			WebUserRole saveUserRole = new WebUserRole();
			saveUserRole.setUserId(userRole.getUserId());
			saveUserRole.setRoleId(userRole.getRoleId());
			saveUserRole.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
			saveUserRole.setCreateUserId(loginUser.getUserId());
			saveUserRole.setCreateTime(LocalDateTime.now());
			webUserRoleMapper.insert(saveUserRole);
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	/**
	 * 获取用户之前的角色，以及当前的角色，判断角色的增删。
	 * @param userRole 用户对应的角色
	 *
	 * @author zhouhui
	 * @since 2022.11.17
	 */
	private void saveOrUpdateUserRole(WebUserRoleEntity userRole) {
		List<WebUserRole> addList = new ArrayList<>(userRole.getRoleList().size());
		List<WebUserRole> delList = new ArrayList<>(userRole.getRoleList().size());
		//获取已经存在的角色
		LambdaQueryWrapper<WebUserRole> userRoleQuery = new LambdaQueryWrapper<>();
		userRoleQuery.eq(WebUserRole::getUserId, userRole.getUserId());
		userRoleQuery.eq(WebUserRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<WebUserRole> oldList = this.baseMapper.selectList(userRoleQuery);
		if(oldList == null || oldList.isEmpty()) {
			//新增所有角色
			Integer userId = userRole.getUserId();
			for(Integer roleId : userRole.getRoleList()) {
				WebUserRole addItem = new WebUserRole();
				addItem.setUserId(userId);
				addItem.setRoleId(roleId);
				addItem.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
				addItem.setCreateTime(LocalDateTime.now());
				addItem.setCreateUserId(UserUtil.getUser().getUserId());
				addList.add(addItem);
			}
		}else {
			compareChangeRole(oldList, userRole, addList, delList);
		}
		//存储数据
		if(!addList.isEmpty()) {
			this.saveBatch(addList);
		}
		//删除的数据
		if(!delList.isEmpty()) {
			delList.forEach(item -> {
				LambdaUpdateWrapper<WebUserRole> update = new LambdaUpdateWrapper<>();
				update.eq(WebUserRole::getMainId, item.getMainId());
				update.set(WebUserRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
				update.set(WebUserRole::getUpdateUserId, UserUtil.getUser().getUserId());
				update.set(WebUserRole::getUpdateTime, LocalDateTime.now());
				this.baseMapper.update(null, update);
			});
		}
	}
	
	/**
	 * 比较修改后用户对应的角色与之前存储用户对应角色有什么变化
	 * @param oldList 存储的用户角色信息
	 * @param userRole 修改后用户对应角色
	 * @param addList 新增列表
	 * @param delList 删除列表
	 *
	 * @author zhouhui
	 * @since 2022.11.17
	 */
	private void compareChangeRole(List<WebUserRole> oldList,WebUserRoleEntity userRole,List<WebUserRole> addList,List<WebUserRole> delList) {
		List<Integer> changeRoleList = userRole.getRoleList();
		List<Integer> tempList = new ArrayList<>(oldList.size());
		oldList.forEach(item -> {
			Integer roleId = item.getRoleId();
			if(!changeRoleList.contains(roleId)) {
				delList.add(item);
			}
			tempList.add(roleId);
		});
		
		Integer userId = userRole.getUserId();
		changeRoleList.forEach(roleId -> {
			if(!tempList.contains(roleId)) {
				WebUserRole addItem = new WebUserRole();
				addItem.setUserId(userId);
				addItem.setRoleId(roleId);
				addItem.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
				addItem.setCreateTime(LocalDateTime.now());
				addItem.setCreateUserId(UserUtil.getUser().getUserId());
				addList.add(addItem);
			}
		});
	}

	@Override
	public R<Object> batchUpdateUserRole(WebUserRoleEntity userRole) {
R<Object> json = new R<>();
		
		WebUser loginUser = UserUtil.getUser();
		
		Integer userId = userRole.getUserId();
		if(userId.equals(MANAGER_USER_ID)) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("不能设置系统管理员的角色");
			return json;
		}
		if(userRole.getRoleList() == null || userRole.getRoleList().isEmpty()) {
			//取消用户对应所有角色
			LambdaUpdateWrapper<WebUserRole> userRoleUpdate = new LambdaUpdateWrapper<>();
			userRoleUpdate.eq(WebUserRole::getUserId, userRole.getUserId());
			userRoleUpdate.eq(WebUserRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			userRoleUpdate.set(WebUserRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
			userRoleUpdate.set(WebUserRole::getUpdateUserId, loginUser.getUserId());
			userRoleUpdate.set(WebUserRole::getUpdateTime, LocalDateTime.now());
			this.update(null, userRoleUpdate);
		}else {
			//判断角色是否都存在
			LambdaQueryWrapper<WebRole> roleQuery = new LambdaQueryWrapper<>();
			roleQuery.in(WebRole::getRoleId, userRole.getRoleList());
			roleQuery.eq(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			List<WebRole> roleList = webRoleMapper.selectList(roleQuery);
			if(roleList == null || roleList.size() != userRole.getRoleList().size()) {
				json.setResultEnum(ResultEnum.ERROR);
				json.setMessage("存在角色已被删除，请重新选择");
				return json;
			}
			
			saveOrUpdateUserRole(userRole);
		}
		//清除用户信息重新登陆
		redisTemplate.delete(AuthenticationEntity.USER_PREFIX + userRole.getUserId());
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
}
