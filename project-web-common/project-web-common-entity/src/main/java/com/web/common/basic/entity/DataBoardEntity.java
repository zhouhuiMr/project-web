package com.web.common.basic.entity;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "数据看板信息对象", description = "数据看板信息对象")
public class DataBoardEntity {

	/** 系统总的用户数 */
	@Schema(title = "系统总的用户数")
	private DataBoardComparisonEntity registerUserCount;
	
	/** 当日登录次数 */
	@Schema(title = "当日登录次数")
	private DataBoardComparisonEntity userLoginCount;
	
	/** 当日接口调用的总次数 */
	@Schema(title = "当日接口调用的总次数")
	private DataBoardComparisonEntity apiTimes;
	
	/** 当日接口调用的失败次数 */
	@Schema(title = "当日接口调用的失败次数")
	private DataBoardComparisonEntity apiErrorTimes;
	
	/** 近期接口调用的统计 */
	@Schema(title = "近期接口调用的统计")
	private List<DataBoardApiEntity> recentApiData;
	
	/** 不同服务调用的统计 */
	@Schema(title = "不同服务调用的统计")
	private List<DataBoardServiceEntity> serviceList;
}
