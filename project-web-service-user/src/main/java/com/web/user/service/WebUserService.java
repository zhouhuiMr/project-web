package com.web.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.web.common.result.R;
import com.web.common.user.entity.WebUser;
import com.web.common.user.entity.WebUserEntity;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
public interface WebUserService extends IService<WebUser> {
	
	/**
	 * 根据查询条件查询用户信息列表
	 * @param user 用户信息查询条件
	 * @return ArrayList<WebUserEntity> 用户信息列表
	 *
	 * @author zhouhui
	 * @since 2022.02.08
	 */
	R<IPage<WebUserEntity>> getUserList(WebUserEntity user);
	
	/**
	 * 获取用户的基本信息、角色信息以及权限信息。
	 * @param code 用户的编号
	 * @return WebUserEntity 用户的基本信息
	 *
	 * @author zhouhui
	 * @since 2022.02.07
	 */
	R<WebUserEntity> getUserInfo(String code);
	
	
	/**
	 * 保存用户信息。<p>
	 * 1、校验用户名是否存在；<p>
	 * 2、如果不存在则生成随机密码；<p>
	 * 3、进行数据的存储；<p>
	 * 4、异步发送邮件信息。
	 * 
	 * @param webuser 将要存储的用户信息
	 * @return WebUserEntity 注册的用户信息
	 *
	 * @author zhouhui
	 * @since 2021.06.20 
	 */
	R<Object> saveUser(WebUserEntity webUser);
	
	/**
	 * 更新用户信息。<p>
	 * <strong>
	 * 注：code 字段无法更新
	 * </strong>
	 * @param webUser 需要更新的用户信息
	 * @return
	 *
	 * @author zhouhui
	 * @since 2022.11.13
	 */
	R<Object> updateUser(WebUserEntity webUser);
	
	/**
	 * 删除用户信息，标记删除。
	 * @param webUser 需要删除的用户信息
	 * @return R<Object> 
	 *
	 * @author zhouhui
	 * @since 2022.11.13
	 */
	R<Object> deleteUser(WebUserEntity webUser);
	
	/**
	 * 重置用户密码
	 * @param webUser 重置密码信息
	 * @return R<Object> 更新结果
	 *
	 * @author zhouhui
	 * @since 2022.12.04
	 */
	R<Object> resetPassword(WebUserEntity webUser);
	
	/**
	 * 用户更新密码
	 * @param webUser 更新的用户密码
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022.12.04
	 */
	R<Object> changePassword(WebUserEntity webUser);
}
