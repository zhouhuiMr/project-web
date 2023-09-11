package com.web.basic.mapper;

import com.web.common.basic.entity.DataBoardComparisonEntity;
import com.web.common.basic.log.entity.BasicOperateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 操作日志记录 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-01-08
 */
@Mapper
public interface BasicOperateLogMapper extends BaseMapper<BasicOperateLog> {

	/**
	 * 统计注册的用户信息
	 * @param today 今天日期
	 * @param yesterday 前一天的日期
	 * @return List<HashMap<String, String>> 数据列表
	 *
	 * @author zhouhui
	 * @since 2023.07.09 
	 */
	DataBoardComparisonEntity statisticsRegisterUser(@Param("today") String today, @Param("yesterday") String yesterday);
}
