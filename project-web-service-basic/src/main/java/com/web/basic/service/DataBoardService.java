package com.web.basic.service;

import java.util.List;
import com.web.common.basic.entity.DataBoardEntity;
import com.web.common.basic.entity.DataBoardSearchParam;
import com.web.common.basic.entity.ServerBasicEntity;
import com.web.common.basic.entity.ServerBoardEntity;
import com.web.common.basic.entity.ServerBoardSearchParam;
import com.web.common.result.R;

public interface DataBoardService {

	/**
	 * 获取数据看板的信息
	 * @param condition 查询条件
	 * @return R<DataBoardEntity> 数据看板信息
	 * 
	 * @author zhouhui
	 * @since 2023.01.24
	 */
	R<DataBoardEntity> showDataBoard(DataBoardSearchParam condition);
	
	/**
	 * 获取服务器的详细信息
	 * @param condition 查询条件
	 * @return R<ServerBoardEntity> 服务器详细信息
	 *
	 * @author zhouhui
	 * @since 2023.07.09
	 */
	R<ServerBoardEntity> showServerInfo(ServerBoardSearchParam condition);
	
	/**
	 * 获取时间范围内的CPU监控数据
	 * @param condition 
	 * @return
	 *
	 * @author zhouhui
	 * @since 2023.07.16
	 */
	R<List<ServerBasicEntity>> monitorData(ServerBoardSearchParam condition);
}
