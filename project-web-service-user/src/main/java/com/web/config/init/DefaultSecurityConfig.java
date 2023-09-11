package com.web.config.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import com.web.common.properties.SecurityLoginEntity;
import com.web.security.filter.AuthCodeFilter;

/**
 * 取消spring security的默认拦截
 * 
 * @since 2020.10.07
 * @author zhouhui
 */
@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private SecurityLoginEntity securityLoginEntity;
	
	@Autowired
	private AuthenticationSuccessHandler formLoginSuccessHandler;
	
	@Autowired
	private AuthenticationFailureHandler formLoginFailureHandler;
	
	@Autowired
	private AuthCodeFilter authCodeFilter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//设置登录信息
		setFormLogin(http);
		//停用即可因为服务不会直接对外开放
		http.csrf().disable();
		http.cors().disable();
		//允许匿名访问
		http.anonymous();
		//不需要鉴权允许访问
		http.authorizeRequests().anyRequest().permitAll();
		//禁用session
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(authCodeFilter, ChannelProcessingFilter.class);
	}
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		StrictHttpFirewall filewall = new StrictHttpFirewall();
		filewall.setAllowSemicolon(true);
		web.httpFirewall(filewall);
		super.configure(web);
	}
	
	/**
	 * 设置登录页面地址、登录接口地址以及请求的参数信息
	 * @param http
	 * @throws Exception
	 *
	 * @author zhouhui
	 * @since 2021.11.20
	 */
	private void setFormLogin(HttpSecurity http) throws Exception {
		FormLoginConfigurer<HttpSecurity> formLogin = http.formLogin();
		formLogin.loginProcessingUrl(securityLoginEntity.getProcessingUrl());
		formLogin.usernameParameter("code");
		formLogin.passwordParameter("password");
		formLogin.permitAll();
		formLogin.successHandler(formLoginSuccessHandler);
		formLogin.failureHandler(formLoginFailureHandler);
	}

	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
