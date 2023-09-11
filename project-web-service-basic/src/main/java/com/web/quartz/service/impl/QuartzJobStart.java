package com.web.quartz.service.impl;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.web.common.basic.quartz.entity.QuartzJobNotify;

/**
 * 系统启动的时候，启动所有的定时任务。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@Component
public class QuartzJobStart implements CommandLineRunner {
	
	@Autowired
	private QuartzJobManage quartzJobManage;

	@Override
	public void run(String... args) throws Exception {
		QuartzJobNotify notify = new QuartzJobNotify();
		notify.setExecIdList(new ArrayList<>());
		notify.setOperateType(1);
		quartzJobManage.quartzJobManage(notify);
	}
}
