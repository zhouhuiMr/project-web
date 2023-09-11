package com.web.user.service.impl;

import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebMenu;
import com.web.common.user.entity.WebRequest;
import com.web.common.user.entity.WebRequestEntity;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.user.mapper.WebMenuMapper;
import com.web.user.mapper.WebRequestMapper;
import com.web.user.publisher.RoleRefreshPublisher;
import com.web.user.service.WebRequestService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 角色对应请求地址 角色对应菜单页面，每个页面中对应的按钮及请求地址。 服务实现类<br>
 * 
 * 注：更新和删除操作需要刷新权限信息，新增不需要，新增数据与角色没有关联。
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Service
public class WebRequestServiceImpl extends ServiceImpl<WebRequestMapper, WebRequest> implements WebRequestService {
	
	@Autowired
	private WebRequestMapper webRequestMapper;
	
	@Autowired
	private WebMenuMapper webMenuMapper;
	
	@Autowired
	private RoleRefreshPublisher roleRefreshPublisher;

	@Override
	public R<IPage<WebRequestEntity>> getRequestList(WebRequestEntity request) {
		R<IPage<WebRequestEntity>> json = new R<>();
		if(StringUtils.hasText(request.getBtDescribe())) {
			request.setBtDescribe("%" + request.getBtDescribe() + "%");
		}
		if(StringUtils.hasText(request.getRequestUrl())) {
			request.setRequestUrl("%" + request.getRequestUrl() + "%");
		}
		if(StringUtils.hasText(request.getPermissionName())) {
			request.setPermissionName("%" + request.getPermissionName() + "%");
		}
		Page<WebRequest> page = new Page<>(request.getPage(), request.getSize());
		IPage<WebRequestEntity> data = webRequestMapper.getRequestList(page,request);
		if (data == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(data);
		return json;
	}

	@Override
	public R<Object> saveRequest(WebRequest request) {
		R<Object> json = new R<>();
		//校验菜单是否存在
		if(!insertOrUpdateVerify(request, json)) {
			return json;
		}
		WebUser user = UserUtil.getUser();
		request.setCreateUserId(user.getUserId());
		request.setCreateTime(LocalDateTime.now());
		int row = webRequestMapper.insert(request);
		if (row <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> updateRequest(WebRequest request) {
		R<Object> json = new R<>();
		//校验菜单是否存在
		if(!insertOrUpdateVerify(request, json)) {
			return json;
		}
		WebUser user = UserUtil.getUser();
		request.setUpdateUserId(user.getUserId());
		request.setUpdateTime(LocalDateTime.now());
		LambdaUpdateWrapper<WebRequest> update = new LambdaUpdateWrapper<>();
		update.eq(WebRequest::getRequestId, request.getRequestId());
		update.eq(WebRequest::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		int row = webRequestMapper.update(request, update);
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
	public R<Object> deleteRquest(WebRequest request) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		LambdaUpdateWrapper<WebRequest> update = new LambdaUpdateWrapper<>();
		update.eq(WebRequest::getRequestId, request.getRequestId());
		update.eq(WebRequest::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(WebRequest::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		update.set(WebRequest::getUpdateUserId, user.getUserId());
		update.set(WebRequest::getUpdateTime, LocalDateTime.now());
		int row = webRequestMapper.update(request, update);
		if (row <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			return json;
		}
		
		//更新缓存角色信息
		roleRefreshPublisher.roleRefresh();
		
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
	
	/**
	 * 请求信息前判断数据是否正确。
	 * 1、判断菜单是否存在。
	 * @param webRequest 请求信息 
	 * @param json 处理结果
	 * @return boolean true校验通过；false校验不通过。
	 *
	 * @author zhouhui
	 * @since 2022-05-18
	 */
	private boolean insertOrUpdateVerify(WebRequest webRequest, R<Object> json) {
		LambdaQueryWrapper<WebMenu> query = new LambdaQueryWrapper<>();
		query.eq(WebMenu::getMenuId, webRequest.getMenuId());
		query.eq(WebMenu::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<WebMenu> dataList = webMenuMapper.selectList(query);
		if(dataList == null || dataList.isEmpty()) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			json.setMessage("选择的菜单不存在");
			return false;
		}
		return true;
	}

	@Override
	public R<List<WebRequestEntity>> getAuthorisedRequest(List<Integer> menuList) {
		R<List<WebRequestEntity>> json = new R<>();
		Integer userId = UserUtil.getUser().getUserId();
		if(userId == null) {
			json.setResultEnum(ResultEnum.NO_USER_ID);
			return json;
		}
		List<WebRequestEntity> requestList = webRequestMapper.getAuthorisedRequest(menuList, userId);
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(requestList);
		return json;
	}

}
