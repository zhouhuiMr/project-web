package com.web.common.basic.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.basic.notepad.entity.BasicNotepad;
import com.web.common.basic.notepad.entity.BasicNotepadEntity;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_BASIC_NAME, contextId = "basicNotepadApi", path = "/basic/notepad")
public interface BasicNotepadApi {
	
	/**
	 * 获取个人每日消息列表
	 * @param notepad 消息信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.02.08
	 */
	@PostMapping(path = "/getSelfNotepad")
	@ResponseBody
	R<IPage<BasicNotepadEntity>> getSelfNotepad(@RequestBody BasicNotepadEntity notepad);

	/**
	 * 保存消息信息
	 * @param notepad 消息信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.02.08
	 */
	@PostMapping(path = "/saveNotepad")
	@ResponseBody
	R<Object> saveNotepad(@Valid @RequestBody BasicNotepad notepad);
	
	/**
	 * 更新消息信息
	 * @param notepad 消息信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.02.08
	 */
	@PostMapping(path = "/updateNotepad")
	@ResponseBody
	R<Object> updateNotepad(@Valid @RequestBody BasicNotepad notepad);
	
	/**
	 * 删除消息信息
	 * @param notepad 消息信息
	 * @return R<Object> 处理结果
	 * 
	 * @author zhouhui
	 * @since 2023.02.08
	 */
	@PostMapping(path = "/deleteNotepad")
	@ResponseBody
	R<Object> deleteNotepad(@RequestBody BasicNotepadEntity notepad);
	
	/**
	 * 获取一个月内每日的消息数量
	 * @param notepad 查询条件
	 * @return Map<String, Integer> 每日消息数量
	 *
	 * @author zhouhui
	 * @since 2023.02.11
	 */
	@PostMapping(path = "/getDailyNotepadOfMonth")
	@ResponseBody
	R<List<Map<String, Integer>>> getDailyNotepadOfMonth(@RequestBody BasicNotepadEntity notepad);
	
	/**
	 * 根据消息的唯一标识获取消息信息
	 * @param notepad 消息的ID
	 * @return BasicNotepadEntity 消息信息
	 *
	 * @author zhouhui
	 * @since 2023.02.14
	 */
	@PostMapping(path = "/getNotepadById")
	@ResponseBody
	R<BasicNotepad> getNotepadById(@RequestBody BasicNotepadEntity notepad);
}
