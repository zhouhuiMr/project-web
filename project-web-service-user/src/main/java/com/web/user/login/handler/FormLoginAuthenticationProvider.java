package com.web.user.login.handler;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.user.mapper.WebUserMapper;

@Component
public class FormLoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider{
	
	@Autowired
	private WebUserMapper webUserMapper;
	
	/** 密码的加密方式   */
	private static final BCryptPasswordEncoder BCrypt = new BCryptPasswordEncoder();

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if(authentication.getCredentials() == null) {
			throw new AuthenticationCredentialsNotFoundException(ResultEnum.PARAMETER_ERROR.getMessage());
		}
		if(!BCrypt.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
			throw new BadCredentialsException(ResultEnum.LOGIN_ERROR.getMessage());
		}
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		WebUser webUser = getUserByCode(username);
		if(webUser == null) {
			this.setHideUserNotFoundExceptions(false);
			throw new UsernameNotFoundException(ResultEnum.LOGIN_ERROR.getMessage());
		}
		ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();
		return new User(webUser.getCode(), webUser.getPassword(), authorityList);
	}
	
	/**
	 * 根据用户的编号获取用户信息
	 * @param code 用户编号
	 * @return WebUser 用户信息
	 *
	 * @author zhouhui
	 * @since 2022.02.07
	 */
	private WebUser getUserByCode(String code) {
		LambdaQueryWrapper<WebUser> query = new LambdaQueryWrapper<>();
		query.eq(WebUser::getCode, code);
		query.eq(WebUser::getStatus, 1);
		WebUser webUser = null;
		try {
			webUser = webUserMapper.selectOne(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(webUser == null) {
			return null;
		}
		return webUser;
	}
	
}
