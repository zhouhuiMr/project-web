package com.web.basic.mapper;

import com.web.common.basic.notepad.entity.BasicNotepad;
import com.web.common.basic.notepad.entity.BasicNotepadEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 记事本（包含代办事项） Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-02-07
 */
@Mapper
public interface BasicNotepadMapper extends BaseMapper<BasicNotepad> {

	/**
	 * 根据条件查询自己记录的事件列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return List<BasicNotepadEntity> 事件列表
	 * 
	 * @author zhouhui
	 * @since 2023.02.09
	 */
	IPage<BasicNotepadEntity> getSelfNotepad(IPage<BasicNotepad> page, @Param("condition") BasicNotepadEntity condition);
	
	/**
	 * 查询一定时间范围内，每日的消息数量
	 * @param condition 查询条件
	 * @return Map<String, Integer> 每日消息数量
	 *
	 * @author zhouhui
	 * @since 2023.02.11
	 */
	List<Map<String, Integer>> getDailyNotepadOfMonth(@Param("condition") BasicNotepadEntity condition);
}
