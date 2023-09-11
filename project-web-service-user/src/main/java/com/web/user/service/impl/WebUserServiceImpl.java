package com.web.user.service.impl;

import com.web.common.basic.info.api.InformationTemplateApi;
import com.web.common.basic.info.entity.MailEntity;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.password.PasswordEmitter;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.ParamVerify;
import com.web.common.tool.Symbols;
import com.web.common.tool.SysDefaultAdmin;
import com.web.common.user.entity.WebRoleEntity;
import com.web.common.user.entity.WebUser;
import com.web.common.user.entity.WebUserDept;
import com.web.common.user.entity.WebUserEntity;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import com.web.user.mapper.WebRoleMapper;
import com.web.user.mapper.WebUserDeptMapper;
import com.web.user.mapper.WebUserMapper;
import com.web.user.service.WebUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Service
public class WebUserServiceImpl extends ServiceImpl<WebUserMapper, WebUser> implements WebUserService {

	@Autowired
	private WebUserMapper webUserMapper;

	@Autowired
	private WebRoleMapper webRoleMapper;
	
	@Autowired
	private WebUserDeptMapper webUserDeptMapper;

	@Autowired
	private InformationTemplateApi informationTemplateApi;

	/** 密码的加密方式 */
	private static final BCryptPasswordEncoder BCrypt = new BCryptPasswordEncoder();

	@Override
	public R<IPage<WebUserEntity>> getUserList(WebUserEntity user) {
		R<IPage<WebUserEntity>> json = new R<>();
		json.setResultEnum(ResultEnum.SUCCESS);

		if (StringUtils.hasText(user.getCode())) {
			user.setCode("%" + user.getCode() + "%");
		}
		if (StringUtils.hasText(user.getNickname())) {
			user.setNickname("%" + user.getNickname() + "%");
		}

		Page<WebUser> page = new Page<>(user.getPage(), user.getSize());
		IPage<WebUserEntity> result = webUserMapper.getUserList(page, user);
		if (result != null) {
			List<WebUserEntity> records = result.getRecords();
			for (WebUserEntity item : records) {
				if (StringUtils.hasText(item.getDeptIds())) {
					String[] ids = item.getDeptIds().split(Symbols.COMMA);
					List<Integer> idList = new ArrayList<>(ids.length);
					for (int i = 0; i < ids.length; i++) {
						idList.add(Integer.valueOf(ids[i]));
					}
					item.setDeptIdList(idList);
				}
			}
		}

		json.setData(result);
		return json;
	}

	@Override
	public R<WebUserEntity> getUserInfo(String code) {
		R<WebUserEntity> json = new R<>();
		json.setResultEnum(ResultEnum.SUCCESS);

		// 获取用户信息
		LambdaQueryWrapper<WebUser> query = new LambdaQueryWrapper<>();
		query.eq(WebUser::getCode, code);
		query.eq(WebUser::getStatus, "1");
		WebUserEntity userEntity = null;
		try {
			WebUser u = webUserMapper.selectOne(query);
			u.setPassword(null);
			userEntity = new WebUserEntity();
			BeanUtils.copyProperties(u, userEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (userEntity == null) {
			json.setResultEnum(ResultEnum.NO_USER_INFO);
			return json;
		}

		// 获取角色信息
		ArrayList<WebRoleEntity> roleList = webRoleMapper.getRolesByUser(userEntity);
		if (roleList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		for (WebRoleEntity item : roleList) {
			userEntity.getRoleList().add(item);
			userEntity.getRoles().add(item.getRoleId());
		}
		// 去掉不需要展示的字段
		userEntity.setCreateTime(null);
		userEntity.setUpdateTime(null);
		userEntity.setCreateUserId(null);
		userEntity.setUpdateUserId(null);

		json.setData(userEntity);
		return json;
	}

	@Override
	public R<Object> saveUser(WebUserEntity webUser) {
		R<Object> json = new R<>();

		WebUser loginUser = UserUtil.getUser();
		
		// 校验用户信息
		verifyParam(webUser);

		// 校验用户名是否已经存在
		LambdaQueryWrapper<WebUser> countQuery = new LambdaQueryWrapper<>();
		countQuery.eq(WebUser::getCode, webUser.getCode());
		Long userNameCount = webUserMapper.selectCount(countQuery);
		if (userNameCount == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		if (userNameCount > 0) {
			json.setResultEnum(ResultEnum.REGISTER_CODE_EXIST);
			return json;
		}
		// 生成密码
		String password = PasswordEmitter.getPassword(8);
		String encodePassword = BCrypt.encode(password);
		webUser.setPassword(encodePassword);
		webUser.setUserType("1");
		webUser.setStatus("1");
		webUser.setCreateUserId(loginUser.getUserId());

		Integer saveRow = webUserMapper.insert(webUser);
		if (saveRow != 1) {
			json.setResultEnum(ResultEnum.DATA_INSERT_ERROR);
			return json;
		}
		
		//保存部门信息
		saveUserDept(webUser, loginUser);

		webUser.setPassword(password);

		// 发送邮件
		if (StringUtils.hasText(webUser.getMail())) {
			try {
				List<String> toList = new ArrayList<>();
				toList.add(webUser.getMail());

				HashMap<String, String> data = new HashMap<>();
				data.put("nickName", webUser.getNickname());
				data.put("code", webUser.getCode());
				data.put("password", webUser.getPassword());

				MailEntity mail = new MailEntity();
				mail.setDataMap(data);
				mail.setToMailAddress(toList);
				informationTemplateApi.sendRegisterSuccessMail(mail);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		json.setResultEnum(ResultEnum.SUCCESS);

		return json;
	}

	/**
	 * 校验用户的信息参数是否正确
	 * 
	 * @param webUser 用户信息
	 *
	 * @author zhouhui
	 * @since 2022.11.28
	 */
	private void verifyParam(WebUserEntity webUser) {
		String mail = webUser.getMail();
		if (StringUtils.hasText(mail) && !ParamVerify.verifyMail(mail)) {
			throw new CustomException(ResultEnum.REGISTER_MAIL_ERROR.getMessage());
		}
	}
	
	/**
	 * 保存用户对应的部门信息
	 * @param webUser 用户对应的部门信息
	 * @param loginUser 登录用户信息
	 * 
	 * @author zhouhui
	 * @since 2023-05-28
	 */
	private void saveUserDept(WebUserEntity webUser, WebUser loginUser) {
		if(webUser.getDeptIdList() != null && !webUser.getDeptIdList().isEmpty()) {
			for (int i = 0; i < webUser.getDeptIdList().size(); i++) {
				WebUserDept userDept = new WebUserDept();
				userDept.setUserId(webUser.getUserId());
				userDept.setDeptId(webUser.getDeptIdList().get(i));
				userDept.setDelFlag(0);
				userDept.setCreateUserId(loginUser.getUserId());
				webUserDeptMapper.insert(userDept);
			}
		}
	}

	@Override
	public R<Object> updateUser(WebUserEntity webUser) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		if (!StringUtils.hasText(webUser.getNickname())) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请填写用户的昵称");
			return json;
		}

		LambdaUpdateWrapper<WebUser> update = new LambdaUpdateWrapper<>();
		update.eq(WebUser::getUserId, webUser.getUserId());
		update.in(WebUser::getStatus, DataSourcesSymbol.USER_STATUS_0, DataSourcesSymbol.USER_STATUS_1);
		update.set(WebUser::getTel, webUser.getTel());
		update.set(WebUser::getMail, webUser.getMail());
		update.set(WebUser::getNickname, webUser.getNickname());
		update.set(WebUser::getProvince, webUser.getProvince());
		update.set(WebUser::getCity, webUser.getCity());
		update.set(WebUser::getDistrict, webUser.getDistrict());
		update.set(WebUser::getBirthday, webUser.getBirthday());
		update.set(WebUser::getIcon, webUser.getIcon());
		update.set(WebUser::getUpdateUserId, user.getUserId());
		update.set(WebUser::getUpdateTime, LocalDateTime.now());
		if (this.update(null, update)) {
			//删除之前用户对应的部门信息
			LambdaUpdateWrapper<WebUserDept> deleteUserDept = new LambdaUpdateWrapper<>();
			deleteUserDept.eq(WebUserDept::getUserId, webUser.getUserId());
			webUserDeptMapper.delete(deleteUserDept);
			
			//保存部门信息
			saveUserDept(webUser, user);
			
			json.setResultEnum(ResultEnum.SUCCESS);
		} else {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
		}
		return json;
	}

	@Override
	public R<Object> deleteUser(WebUserEntity webUser) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		if (SysDefaultAdmin.SYSTEM_ADMIN_ID == webUser.getUserId().intValue()) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("默认系统管理员不能删除");
			return json;
		}

		LambdaUpdateWrapper<WebUser> update = new LambdaUpdateWrapper<>();
		update.eq(WebUser::getUserId, webUser.getUserId());
		update.set(WebUser::getStatus, DataSourcesSymbol.USER_STATUS_2);
		update.set(WebUser::getUpdateUserId, user.getUserId());
		update.set(WebUser::getUpdateTime, LocalDateTime.now());
		if (this.update(null, update)) {
			json.setResultEnum(ResultEnum.SUCCESS);
		} else {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
		}
		return json;
	}

	@Override
	public R<Object> resetPassword(WebUserEntity webUser) {
		R<Object> json = new R<>();
		WebUser loginUser = UserUtil.getUser();

		Integer userId = webUser.getUserId();
		if (userId == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择需要重置密码的用户");
			return json;
		}
		if (SysDefaultAdmin.SYSTEM_ADMIN_ID == userId.intValue()) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("不能重置系统管理员密码");
			return json;
		}
		// 生成新的密码
		String password = PasswordEmitter.getPassword(8);
		String encodePassword = BCrypt.encode(password);

		// 更新密码
		LambdaUpdateWrapper<WebUser> update = new LambdaUpdateWrapper<>();
		update.eq(WebUser::getUserId, webUser.getUserId());
		update.set(WebUser::getPassword, encodePassword);
		update.set(WebUser::getUpdateUserId, loginUser.getUserId());
		update.set(WebUser::getUpdateTime, LocalDateTime.now());
		this.baseMapper.update(null, update);

		// 查询用户信息发送邮件
		WebUser user = baseMapper.selectById(userId);
		if (StringUtils.hasText(user.getMail())) {
			try {
				List<String> toList = new ArrayList<>();
				toList.add(user.getMail());

				HashMap<String, String> data = new HashMap<>();
				data.put("nickName", user.getNickname());
				data.put("code", user.getCode());
				data.put("password", password);

				MailEntity mail = new MailEntity();
				mail.setDataMap(data);
				mail.setToMailAddress(toList);
				informationTemplateApi.resetPasswordMail(mail);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	@Override
	public R<Object> changePassword(WebUserEntity webUser) {
		R<Object> json = new R<>();
		WebUser loginUser = UserUtil.getUser();
		if (!StringUtils.hasText(webUser.getOldPassword())) {
			json.setResultEnum(ResultEnum.REGISTER_NO_PASSWORD);
			return json;
		}
		if (!StringUtils.hasText(webUser.getNewPassword())) {
			json.setResultEnum(ResultEnum.REGISTER_NO_NEWPASSWORD);
			return json;
		}
		if (!ParamVerify.verifyPassword(webUser.getNewPassword())) {
			json.setResultEnum(ResultEnum.REGISTER_PASSWORD_FORMAT);
			return json;
		}
		// 判断旧密码是否正确
		LambdaQueryWrapper<WebUser> query = new LambdaQueryWrapper<>();
		query.eq(WebUser::getUserId, loginUser.getUserId());
		query.ne(WebUser::getStatus, DataSourcesSymbol.USER_STATUS_2);
		WebUser user = baseMapper.selectOne(query);
		if (user == null) {
			json.setResultEnum(ResultEnum.NO_USER_INFO);
			return json;
		}
		if (!BCrypt.matches(webUser.getOldPassword(), user.getPassword())) {
			json.setResultEnum(ResultEnum.REGISTER_OLD_PASSWORD_ERROR);
			return json;
		}
		// 更新密码
		String encodePassword = BCrypt.encode(webUser.getNewPassword());
		LambdaUpdateWrapper<WebUser> update = new LambdaUpdateWrapper<>();
		update.eq(WebUser::getUserId, loginUser.getUserId());
		update.set(WebUser::getPassword, encodePassword);
		update.set(WebUser::getUpdateTime, LocalDateTime.now());
		update.set(WebUser::getUpdateUserId, loginUser.getUserId());
		baseMapper.update(null, update);

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
}
