package com.web.basic.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.basic.service.impl.BasicOperateLogServiceImpl;
import com.web.common.basic.api.BasicOperateLogApi;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.web.common.basic.log.entity.BasicOperateLogEntity;
import com.web.common.basic.log.entity.ProjectWebLog;
import com.web.common.result.R;
import com.web.service.handler.log.IgnoreOperateLogs;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 操作日志记录 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-01-08
 */
@Tag(name = "操作日志功能")
@Controller
@RequestMapping("/basic/log")
public class BasicOperateLogController implements BasicOperateLogApi{
	
	@Autowired
	private BasicOperateLogServiceImpl basicOperateLogServiceImpl;

	@IgnoreOperateLogs
	@Operation(summary = "保存操作记录")
	@Override
	public R<Object> saveOperateLog(BasicOperateLog operateLog) {
		return basicOperateLogServiceImpl.saveOperateLog(operateLog);
	}

	@Operation(summary = "获取操作记录列表")
	@Override
	public R<IPage<ProjectWebLog>> getOperateList(BasicOperateLogEntity condition) {
		return basicOperateLogServiceImpl.getOperateLogList(condition);
	}
}
