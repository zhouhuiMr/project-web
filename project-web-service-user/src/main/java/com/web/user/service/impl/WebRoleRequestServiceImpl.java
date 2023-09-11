package com.web.user.service.impl;

import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebRequest;
import com.web.common.user.entity.WebRole;
import com.web.common.user.entity.WebRoleRequest;
import com.web.common.user.entity.WebRoleRequestEntity;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.user.mapper.WebRequestMapper;
import com.web.user.mapper.WebRoleMapper;
import com.web.user.mapper.WebRoleRequestMapper;
import com.web.user.publisher.RoleRefreshPublisher;
import com.web.user.service.WebRoleRequestService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色对应的请求和按钮的权限 角色对应的请求和按钮的权限。 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-09-17
 */
@Service
public class WebRoleRequestServiceImpl extends ServiceImpl<WebRoleRequestMapper, WebRoleRequest> implements WebRoleRequestService {
	
	@Autowired
	private WebRoleMapper webRoleMapper;
	
	@Autowired
	private WebRequestMapper webRequestMapper;
	
	@Autowired
	private RoleRefreshPublisher roleRefreshPublisher;

	@Override
	public R<List<WebRoleRequestEntity>> getAuthRequest(WebRoleRequestEntity condition) {
		R<List<WebRoleRequestEntity>> json = new R<>();
		List<WebRoleRequestEntity> dataList = baseMapper.getAuthRequest(condition);
		if(dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}

	@Override
	public R<Object> saveOrUpdateRoleRequest(WebRoleRequest roleRequest) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		
		//判断角色是否存在
		LambdaQueryWrapper<WebRole> queryRole = new LambdaQueryWrapper<>();
		queryRole.eq(WebRole::getRoleId, roleRequest.getRoleId());
		queryRole.eq(WebRole::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<WebRole> roleList = webRoleMapper.selectList(queryRole);
		if(roleList == null || roleList.isEmpty()) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("未查询到角色信息");
			return json;
		}
		//判断接口是否存在
		LambdaQueryWrapper<WebRequest> queryRequest = new LambdaQueryWrapper<>();
		queryRequest.eq(WebRequest::getRequestId, roleRequest.getRequestId());
		queryRequest.eq(WebRequest::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<WebRequest> requestList = webRequestMapper.selectList(queryRequest);
		if(requestList == null || requestList.isEmpty()) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("未查询到接口信息");
			return json;
		}
		//判断角色对应的接口是否已经存在
		LambdaQueryWrapper<WebRoleRequest> query = new LambdaQueryWrapper<>();
		query.eq(WebRoleRequest::getRoleId, roleRequest.getRoleId());
		query.eq(WebRoleRequest::getRequestId, roleRequest.getRequestId());
		List<WebRoleRequest> roleRequestList = baseMapper.selectList(query);
		if(roleRequestList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		int changeRow = 0;
		if(roleRequestList.isEmpty()) {
			//新增
			roleRequest.setCreateUserId(user.getUserId());
			roleRequest.setCreateTime(LocalDateTime.now());
			roleRequest.setUpdateUserId(user.getUserId());
			roleRequest.setUpdateTime(LocalDateTime.now());
			changeRow = baseMapper.insert(roleRequest);
		}else {
			//更新
			roleRequest.setUpdateUserId(user.getUserId());
			roleRequest.setUpdateTime(LocalDateTime.now());
			LambdaUpdateWrapper<WebRoleRequest> updateQuery = new LambdaUpdateWrapper<>();
			updateQuery.eq(WebRoleRequest::getRequestId, roleRequest.getRequestId());
			updateQuery.eq(WebRoleRequest::getRoleId, roleRequest.getRoleId());
			changeRow = baseMapper.update(roleRequest, updateQuery);
		}
		//更新缓存
		if(changeRow > 0) {
			roleRefreshPublisher.roleRefresh(roleRequest.getRoleId());
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<HashMap<String, HashMap<String, String>>> getAllowRequest(List<Integer> roleList) {
		R<HashMap<String, HashMap<String, String>>> json = new R<>();
		
		List<WebRoleRequestEntity> dataList = baseMapper.getAllowRequest(roleList);
		if(dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();
		for(WebRoleRequestEntity item : dataList) {
			String roleId = item.getRoleId() + "";
			String requestUrl = item.getRequestUrl();
			String isAllow = item.getIsAllow();
			
			dataMap.computeIfAbsent(roleId, key -> new HashMap<String, String>()).put(requestUrl, isAllow);
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataMap);
		return json;
	}

}
