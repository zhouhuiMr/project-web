package com.web.common.basic.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.entity.DataBoardEntity;
import com.web.common.basic.entity.DataBoardSearchParam;
import com.web.common.basic.entity.ServerBasicEntity;
import com.web.common.basic.entity.ServerBoardEntity;
import com.web.common.basic.entity.ServerBoardSearchParam;
import com.web.common.properties.ServerConfigEntiy;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "dataBoardApi", path = "/basic/dataBoard")
public interface DataBoardApi {

	/**
	 * 获取数据看板的信息
	 * @param condition 查询条件
	 * @return DataBoardEntity 数据面板信息
	 * 
	 * @author zhouhui
	 * @since 2023.01.24
	 */
	@PostMapping(path = "/show")
	@ResponseBody
	R<DataBoardEntity> showDataBoard(@RequestBody DataBoardSearchParam condition);
	
	/**
	 * 获取所有的服务器信息
	 * @return List<ServerConfigEntiy> 服务器信息列表
	 *
	 * @author zhouhui
	 * @since 2023.07.09 
	 */
	@PostMapping(path = "/showServerList")
	@ResponseBody
	R<List<ServerConfigEntiy>> showServerList();
	
	/**
	 * 获取服务器的详细信息
	 * @param condition 查询条件
	 * @return ServerBoardEntity 服务器信息
	 *
	 * @author zhouhui
	 * @since 2023.07.09 
	 */
	@PostMapping(path = "/showServerInfo")
	@ResponseBody
	R<ServerBoardEntity> showServerInfo(@RequestBody ServerBoardSearchParam condition);
	
	/**
	 * 获取时间范围内的CPU监控数据
	 * @param condition 查询条件
	 * @return List<ServerCpuEntity> CPU监控数据
	 *
	 * @author zhouhui
	 * @since 2023.07.16
	 */
	@PostMapping(path = "/monitorData")
	@ResponseBody
	R<List<ServerBasicEntity>> monitorData(@RequestBody ServerBoardSearchParam condition);
}
