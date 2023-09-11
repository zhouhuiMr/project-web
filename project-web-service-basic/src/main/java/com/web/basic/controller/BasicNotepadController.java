package com.web.basic.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.basic.service.impl.BasicNotepadServiceImpl;
import com.web.common.basic.api.BasicNotepadApi;
import com.web.common.basic.notepad.entity.BasicNotepad;
import com.web.common.basic.notepad.entity.BasicNotepadEntity;
import com.web.common.result.R;
import com.web.service.filter.xss.XssIgnore;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 记事本（包含代办事项） 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-02-07
 */
@Tag(name = "记事本功能")
@Controller
@RequestMapping("/basic/notepad")
public class BasicNotepadController implements BasicNotepadApi{
	
	@Autowired
	private BasicNotepadServiceImpl basicNotepadServiceImpl;
	
	@Operation(summary = "获取个人每日消息")
	@Override
	public R<IPage<BasicNotepadEntity>> getSelfNotepad(BasicNotepadEntity notepad) {
		return basicNotepadServiceImpl.getSelfNotepad(notepad);
	}
	
	@XssIgnore
	@Operation(summary = "保存消息")
	@Override
	public R<Object> saveNotepad(BasicNotepad notepad) {
		return basicNotepadServiceImpl.saveNotepad(notepad);
	}

	@XssIgnore
	@Operation(summary = "更新消息")
	@Override
	public R<Object> updateNotepad(BasicNotepad notepad) {
		return basicNotepadServiceImpl.updateNotepad(notepad);
	}

	@Operation(summary = "删除消息")
	@Override
	public R<Object> deleteNotepad(BasicNotepadEntity notepad) {
		return basicNotepadServiceImpl.deleteNotepad(notepad);
	}

	@Operation(summary = "获取一个月内每日的消息数量")
	@Override
	public R<List<Map<String, Integer>>> getDailyNotepadOfMonth(BasicNotepadEntity notepad) {
		return basicNotepadServiceImpl.getDailyNotepadOfMonth(notepad);
	}

	@Operation(summary = "根据消息的唯一标识获取消息信息")
	@Override
	public R<BasicNotepad> getNotepadById(BasicNotepadEntity notepad) {
		return basicNotepadServiceImpl.getNotepadById(notepad);
	}

}
