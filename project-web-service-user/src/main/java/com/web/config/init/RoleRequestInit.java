package com.web.config.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.web.user.publisher.RoleRefreshPublisher;


/**
 * 角色对应的接口数据的初始化。<p>
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class RoleRequestInit implements CommandLineRunner{
	
	@Autowired
	private RoleRefreshPublisher roleRefreshPublisher;
	
	@Override
	public void run(String... args) throws Exception {
		roleRefreshPublisher.roleRefresh(false);
	}
}
