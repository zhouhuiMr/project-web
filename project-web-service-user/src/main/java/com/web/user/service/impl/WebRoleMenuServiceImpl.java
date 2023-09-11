package com.web.user.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.RoleAuthMenu;
import com.web.common.user.entity.WebMenu;
import com.web.common.user.entity.WebMenuEntity;
import com.web.common.user.entity.WebRole;
import com.web.common.user.entity.WebRoleMenu;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.user.mapper.WebMenuMapper;
import com.web.user.mapper.WebRoleMapper;
import com.web.user.mapper.WebRoleMenuMapper;
import com.web.user.service.WebRoleMenuService;

/**
 * <p>
 * 角色与菜单对应关系 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-09-14
 */
@Service
public class WebRoleMenuServiceImpl extends ServiceImpl<WebRoleMenuMapper, WebRoleMenu> implements WebRoleMenuService{
	
	@Autowired
	private WebMenuMapper webMenuMapper;
	
	@Autowired
	private WebRoleMapper webRoleMapper;
	
	@Autowired
	private WebRoleMenuMapper webRoleMenuMapper;
	
	@Override
	public R<List<RoleAuthMenu>> getAuthMenuList(WebRoleMenu roleMenu) {
		R<List<RoleAuthMenu>> json = new R<>();
		List<WebMenuEntity> menuList = webMenuMapper.getAuthMenuList(roleMenu);
		if(menuList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		List<RoleAuthMenu> dataList = buildMenuTree(menuList);
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}
	
	/**
	 * 构建菜单的树结构
	 * @param menuList 查询到的菜单列表
	 * @return List<RoleAuthMenu> 父子关系的菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-09-17
	 */
	private List<RoleAuthMenu> buildMenuTree(List<WebMenuEntity> menuList){
		List<RoleAuthMenu> dataList = new ArrayList<>(menuList.size());
		Map<Integer, RoleAuthMenu> roleMenuMap = new HashMap<>(menuList.size());
		List<RoleAuthMenu> tempList = new ArrayList<>(menuList.size());
		
		for(WebMenuEntity item : menuList) {
			String isAllow = item.getIsAllow();
			if(isAllow == null) {
				//不允许访问
				item.setIsAllow("0");
			}else {
				//允许访问
				item.setIsAllow("1");
			}
			RoleAuthMenu menuItem = new RoleAuthMenu();
			BeanUtils.copyProperties(item,menuItem);
			
			Integer menuId = menuItem.getMenuId();
			Integer parentId = item.getParentId();
			
			roleMenuMap.put(menuId, menuItem);
			
			if(parentId <= 0) {
				dataList.add(menuItem);
				continue;
			}
			RoleAuthMenu roleMenu = roleMenuMap.get(parentId);
			if(roleMenu != null) {
				if(roleMenu.getChildren() == null) {
					roleMenu.setChildren(new ArrayList<>());
				}
				roleMenu.getChildren().add(menuItem);
			}else {
				tempList.add(menuItem);
			}
		}
		setMenuTree(tempList, roleMenuMap);
		return dataList;
	}
	
	/**
	 * 组装最后的数据，形成树
	 * @param tempList 权限角色列表
	 * @param roleMenuMap 角色对应的菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-09-17
	 */
	private void setMenuTree(List<RoleAuthMenu> tempList, Map<Integer, RoleAuthMenu> roleMenuMap) {
		for(RoleAuthMenu item : tempList) {
			Integer parentId = item.getParentId();
			
			RoleAuthMenu parentMenu = roleMenuMap.get(parentId);
			if(parentMenu != null) {
				if(parentMenu.getChildren() == null) {
					parentMenu.setChildren(new ArrayList<>());
				}
				parentMenu.getChildren().add(item);
			}
		}
	}

	@Override
	public R<Object> saveOrUpdateRoleMenu(WebRoleMenu roleMenu) {
		R<Object> json = new R<>();
		//校验角色是否存在
		Integer roleId = roleMenu.getRoleId();
		LambdaQueryWrapper<WebRole> roleQuery = new LambdaQueryWrapper<>();
		roleQuery.eq(WebRole::getRoleId, roleId);
		roleQuery.eq(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		Long roleCount = webRoleMapper.selectCount(roleQuery);
		if(roleCount == null || roleCount.intValue() != 1) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			json.setMessage("角色不存在");
			return json;
		}
		//校验菜单是否存在
		Integer menuId = roleMenu.getMenuId();
		LambdaQueryWrapper<WebMenu> menuQuery = new LambdaQueryWrapper<>();
		menuQuery.eq(WebMenu::getParentId, menuId);
		menuQuery.eq(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		Long menuCount = webMenuMapper.selectCount(menuQuery);
		if(menuCount == null || roleCount.intValue() != 1) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			json.setMessage("菜单不存在");
			return json;
		}
		//查询角色对应的菜单是否存在（并发会出现重复数据，不判断是否删除，存在更新）
		LambdaQueryWrapper<WebRoleMenu> roleMenuQuery = new LambdaQueryWrapper<>();
		roleMenuQuery.eq(WebRoleMenu::getRoleId, roleId);
		roleMenuQuery.eq(WebRoleMenu::getMenuId, menuId);
		List<WebRoleMenu> roleMenuList = webRoleMenuMapper.selectList(roleMenuQuery);
		if(roleMenuList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			json.setMessage("查询角色对应菜单异常");
			return json;
		}
		WebUser user = UserUtil.getUser();
		int changeCount = 0;
		if(!roleMenuList.isEmpty()) {
			//更新数据
			WebRoleMenu roleMenuItem = roleMenuList.get(0);
			roleMenuItem.setDelFlag(roleMenu.getDelFlag());
			roleMenuItem.setUpdateUserId(user.getUserId());
			roleMenuItem.setUpdateTime(LocalDateTime.now());
			
			LambdaQueryWrapper<WebRoleMenu> update = new LambdaQueryWrapper<>();
			update.eq(WebRoleMenu::getMainId, roleMenuItem.getMainId());
			changeCount = webRoleMenuMapper.update(roleMenuItem, update);
			if(changeCount != 1) {
				json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
				return json;
			}
		}else {
			//新增数据
			WebRoleMenu saveItem = new WebRoleMenu();
			saveItem.setRoleId(roleId);
			saveItem.setMenuId(menuId);
			saveItem.setCreateUserId(user.getUserId());
			saveItem.setDelFlag(roleMenu.getDelFlag());
			changeCount = webRoleMenuMapper.insert(saveItem);
			if(changeCount != 1) {
				json.setResultEnum(ResultEnum.DATA_INSERT_ERROR);
				return json;
			}
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

}
