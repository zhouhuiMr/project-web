package com.web.config.init;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * 取消spring security的默认拦截
 * 
 * @since 2020.10.07
 * @author zhouhui
 */
@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//停用即可因为服务不会直接对外开放
		http.csrf().disable();
		http.cors().disable();
		//允许匿名访问
		http.anonymous();
		//不需要鉴权允许访问
		http.authorizeRequests().anyRequest().permitAll();
		//禁用session
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		return super.userDetailsService();
	}
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		StrictHttpFirewall filewall = new StrictHttpFirewall();
		filewall.setAllowSemicolon(true);
		web.httpFirewall(filewall);
		super.configure(web);
	}
}
