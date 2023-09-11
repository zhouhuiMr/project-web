package com.web.common.basic.api;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.web.common.basic.log.entity.BasicOperateLogEntity;
import com.web.common.basic.log.entity.ProjectWebLog;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "basicOperateLogApi", path = "/basic/log")
public interface BasicOperateLogApi {

	/**
	 * 记录用户的操作日志
	 * @param operateLog 日志信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(path = "/saveOperateLog")
	@ResponseBody
	R<Object> saveOperateLog(@Valid @RequestBody BasicOperateLog operateLog);
	
	/**
	 * 获取用户的操作日志列表
	 * @param condition 查询条件
	 * @return R<IPage<BasicOperateLogEntity>> 数据列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@PostMapping(path = "/getOperateList")
	@ResponseBody
	R<IPage<ProjectWebLog>> getOperateList(@RequestBody BasicOperateLogEntity condition);
}
