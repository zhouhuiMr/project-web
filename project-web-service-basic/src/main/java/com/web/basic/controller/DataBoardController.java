package com.web.basic.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.web.basic.service.DataBoardService;
import com.web.common.basic.api.DataBoardApi;
import com.web.common.basic.entity.DataBoardEntity;
import com.web.common.basic.entity.DataBoardSearchParam;
import com.web.common.basic.entity.ServerBasicEntity;
import com.web.common.basic.entity.ServerBoardEntity;
import com.web.common.basic.entity.ServerBoardSearchParam;
import com.web.common.properties.ServerConfigEntiy;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.service.aliyun.AliyunConfigEntity;
import com.web.service.handler.log.IgnoreOperateLogs;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "数据看板功能")
@Controller
@RequestMapping(path = "/basic/dataBoard")
public class DataBoardController implements DataBoardApi{
	
	@Autowired
	private DataBoardService dataBoardServiceImpl;
	
	@Autowired
	private AliyunConfigEntity aliyunConfigEntity;

	@IgnoreOperateLogs
	@Operation(summary = "获取数据看板的信息")
	@Override
	public R<DataBoardEntity> showDataBoard(DataBoardSearchParam condition) {
		return dataBoardServiceImpl.showDataBoard(condition);
	}

	@IgnoreOperateLogs
	@Operation(summary = "获取所有的服务器信息")
	@Override
	public R<List<ServerConfigEntiy>> showServerList() {
		R<List<ServerConfigEntiy>> json = new R<>();
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(aliyunConfigEntity.getEcs());
		return json;
	}

	@IgnoreOperateLogs
	@Operation(summary = "获取服务器的详细信息")
	@Override
	public R<ServerBoardEntity> showServerInfo(ServerBoardSearchParam condition) {
		return dataBoardServiceImpl.showServerInfo(condition);
	}

	@IgnoreOperateLogs
	@Operation(summary = "获取时间范围内的监控数据")
	@Override
	public R<List<ServerBasicEntity>> monitorData(ServerBoardSearchParam condition) {
		return dataBoardServiceImpl.monitorData(condition);
	}
}
