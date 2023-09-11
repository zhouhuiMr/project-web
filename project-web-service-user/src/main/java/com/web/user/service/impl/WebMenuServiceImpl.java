package com.web.user.service.impl;

import com.web.common.basic.api.BasicSysDictApi;
import com.web.common.basic.entity.BasicSysDict;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.dictionary.DictionaryError;
import com.web.common.dictionary.VerifyDictionary;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.Symbols;
import com.web.common.user.entity.UserMenu;
import com.web.common.user.entity.WebMenu;
import com.web.common.user.entity.WebMenuEntity;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.user.mapper.WebMenuMapper;
import com.web.user.publisher.RoleRefreshPublisher;
import com.web.user.service.WebMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 对应的菜单 服务实现类<br>
 * 注：更新和删除操作需要刷新权限信息，新增不需要，新增数据与角色没有关联。
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-17
 */
@Service
public class WebMenuServiceImpl extends ServiceImpl<WebMenuMapper, WebMenu> implements WebMenuService {

	public static final Integer ROOT_MENU = -1;
	
	public static final int SYSTEM_DEFAULT_MENU = 0;

	@Autowired
	private WebMenuMapper webMenuMapper;
	
	@Autowired
	private RoleRefreshPublisher roleRefreshPublisher;
	
	@Autowired
	private BasicSysDictApi sysDictApi;

	@Override
	public R<IPage<WebMenuEntity>> getMenuList(WebMenuEntity webMenu) {
		R<IPage<WebMenuEntity>> json = new R<>();
		if (StringUtils.hasText(webMenu.getMenuName())) {
			webMenu.setMenuName("%" + webMenu.getMenuName() + "%");
		}
		Page<WebMenu> page = new Page<>(webMenu.getPage(), webMenu.getSize());
		IPage<WebMenuEntity> data = webMenuMapper.getMenuList(page, webMenu);
		if (data == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(data);
		return json;
	}

	@Override
	public R<Object> saveMenu(WebMenu webMenu) {
		R<Object> json = new R<>();
		
		if(!insertOrUpdateVerify(webMenu, json)) {
			return json;
		}
		WebUser user = UserUtil.getUser();
		webMenu.setCreateUserId(user.getUserId());
		
		int row = webMenuMapper.insert(webMenu);
		if (row <= 0) {
			json.setResultEnum(ResultEnum.DATA_INSERT_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> updateMenu(WebMenu webMenu) {
		R<Object> json = new R<>();
		if(!insertOrUpdateVerify(webMenu, json)) {
			return json;
		}
		
		WebUser user = UserUtil.getUser();
		
		webMenu.setDelFlag(null);
		webMenu.setUpdateUserId(user.getUserId());
		webMenu.setUpdateTime(LocalDateTime.now());
		LambdaUpdateWrapper<WebMenu> update = new LambdaUpdateWrapper<>();
		update.eq(WebMenu::getMenuId, webMenu.getMenuId());
		update.eq(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		int row = webMenuMapper.update(webMenu, update);
		if (row <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		
		//更新缓存角色信息
		roleRefreshPublisher.roleRefresh();
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> delMenu(WebMenu webMenu) {
		R<Object> json = new R<>();
		
		if(webMenu.getMenuId().intValue() <= SYSTEM_DEFAULT_MENU) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("系统默认菜单不能删除");
			return json;
		}
		
		//判断是否存在子菜单
		LambdaQueryWrapper<WebMenu> query = new LambdaQueryWrapper<>();
		query.eq(WebMenu::getParentId, webMenu.getMenuId());
		query.eq(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		Long childrenCount = webMenuMapper.selectCount(query);
		if(childrenCount == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		if(childrenCount.intValue() > 0) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			json.setMessage("当前菜单存在子菜单，不能进行删除");
			return json;
		}
		
		WebUser user = UserUtil.getUser();
		
		webMenu.setUpdateTime(LocalDateTime.now());
		webMenu.setUpdateUserId(user.getUserId());
		LambdaUpdateWrapper<WebMenu> update = new LambdaUpdateWrapper<>();
		update.eq(WebMenu::getMenuId, webMenu.getMenuId());
		update.eq(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		int row = webMenuMapper.update(webMenu, update);
		if (row <= 0) {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
			return json;
		}
		
		//更新缓存角色信息
		roleRefreshPublisher.roleRefresh();
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<List<UserMenu>> getUserMenu() {
		R<List<UserMenu>> json = new R<>();
		WebUser user = UserUtil.getUser();
		List<UserMenu> menuList = webMenuMapper.getUserMenu(user.getUserId());
		if(menuList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		List<UserMenu> userMenuList = new ArrayList<>(menuList.size());
		Map<Integer, UserMenu> menuMap = new HashMap<>(menuList.size());
		List<UserMenu> tempMenuList = new ArrayList<>(menuList.size());
		
		//构建菜单树形结构
		buildMenuTree(userMenuList, menuMap, tempMenuList, menuList);
		
		
		//针对父子菜单id顺序不一致需要单独处理
		for(UserMenu item : tempMenuList) {
			Integer parentId = item.getParentId();
			
			UserMenu parentMenu = menuMap.get(parentId);
			if(parentMenu != null) {
				if(parentMenu.getChildren() == null) {
					parentMenu.setChildren(new ArrayList<>());
				}
				item.setIndex(getMenuIndex(menuMap, item));
				parentMenu.getChildren().add(item);
			}
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(userMenuList);
		return json;
	}
	
	/**
	 * 根据菜单的树结构
	 * @param userMenuList 用户的菜单列表
	 * @param menuMap 菜单的Map集合
	 * @param tempMenuList 临时菜单集合
	 * @param menuList 菜单列表
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	private void buildMenuTree(List<UserMenu> userMenuList, Map<Integer, UserMenu> menuMap, List<UserMenu> tempMenuList, List<UserMenu> menuList) {
		for(UserMenu item : menuList) {
			Integer menuId = item.getMenuId();
			Integer parentId = item.getParentId();
			
			menuMap.put(menuId, item);
			
			if(parentId <= 0) {
				item.setIndex(menuId + "");
				userMenuList.add(item);
				continue;
			}
			UserMenu parentMenu = menuMap.get(parentId);
			if(parentMenu != null) {
				if(parentMenu.getChildren() == null) {
					parentMenu.setChildren(new ArrayList<>());
				}
				item.setIndex(getMenuIndex(menuMap, item));
				parentMenu.getChildren().add(item);
			}else {
				//存在子菜单的id大于父菜单的id，需要进行单独处理
				tempMenuList.add(item);
			}
		}
	}
	
	/**
	 * 设置菜单的索引值，三级菜单格式1-2-1。
	 * 从后往前查找菜单，并拼接菜单index
	 * @param menuMap 菜单Id对应的菜单信息
	 * @param menu 当前的菜单
	 * @return 
	 *
	 * @author zhouhui
	 * @since 2022-06-12
	 */
	private String getMenuIndex(Map<Integer, UserMenu> menuMap, UserMenu menu) {
		int parentId =  menu.getParentId();
		if(parentId <= 0) {
			return menu.getMenuId() + "";
		}
		UserMenu parentMenu = menuMap.get(parentId);
		if(parentMenu == null) {
			return "";
		}
		
		StringBuilder index = new StringBuilder();
		index.append("-" + menu.getMenuId());
		while(parentId > 0) {
			index.insert(0, parentMenu.getMenuId());
			parentId = parentMenu.getParentId();
			if(parentId > 0) {
				index.insert(0, "-");
				parentMenu = menuMap.get(parentId);
			}
		}
		return index.toString();
	}

	/**
	 * 菜单新增或者更新时，进行字段属性的校验。
	 * @param webMenu 菜单信息
	 * @param json 处理结果
	 * @return boolean true校验通过；false校验不通过
	 *
	 * @author zhouhui
	 * @since 2022-05-08
	 */
	private boolean insertOrUpdateVerify(WebMenu webMenu, R<Object> json) {
		// 判断父菜单ID是否存在
		if (!isRightParentMenu(webMenu.getParentId())) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("选择的父菜单不存在");
			return false;
		}

		// 校验填写的参数是否正确
		String[] types = {"menu_level", "menu_scope", "menu_type", "status"};
		List<String> typeList = Arrays.asList(types);
		R<List<BasicSysDict>> result = sysDictApi.getDictListByType(typeList);
		if(!ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
			json.setResultEnum(ResultEnum.FEIGN_ERROR);
			json.setMessage(result.getMessage());
			return false;
		}
		List<BasicSysDict> dictList = result.getData();

		List<DictionaryError> errorList = VerifyDictionary.operateDict(webMenu, dictList);
		if (!errorList.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < errorList.size(); i++) {
				if (i > 0) {
					builder.append(Symbols.CN_SEMICOLON);
				}
				builder.append(errorList.get(i).getMessage());
			}
			json.setResultEnum(ResultEnum.DICTIONARY_VERIFY_ERROR);
			json.setMessage(builder.toString());
			return false;
		}
		
		return true;
	}

	/**
	 * 判断父菜单的ID是否存在
	 * 
	 * @param parentMenuId
	 * @return boolean true校验通过；false父菜单ID不存在
	 *
	 * @author zhouhui
	 * @since 2022-05-08
	 */
	private boolean isRightParentMenu(Integer parentMenuId) {
		if (Objects.equals(ROOT_MENU, parentMenuId)) {
			return true;
		} else {
			LambdaQueryWrapper<WebMenu> query = new LambdaQueryWrapper<>();
			query.eq(WebMenu::getMenuId, parentMenuId);
			query.eq(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			WebMenu menu = webMenuMapper.selectOne(query);
			if (menu == null) {
				return false;
			}
		}
		return true;
	}
}
