package com.web.basic.service.impl;

import com.web.basic.mapper.BasicNotepadMapper;
import com.web.common.basic.notepad.entity.BasicNotepad;
import com.web.common.basic.notepad.entity.BasicNotepadEntity;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.ParamVerify;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 记事本（包含代办事项） 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-02-07
 */
@Service
public class BasicNotepadServiceImpl extends ServiceImpl<BasicNotepadMapper, BasicNotepad> {

	/** 创建日期的格式错误，请使用yyyy-MM-dd */
	private static final String DATEFORMATTER_ERROR_MESSAGE = "创建日期的格式错误，请使用yyyy-MM-dd";

	/**
	 * 查询个人当日的事件列表
	 * 
	 * @param condition 查询条件
	 * @return List<BasicNotepadEntity>
	 * 
	 * @author zhouhui
	 * @since 2023.02.09
	 */
	public R<IPage<BasicNotepadEntity>> getSelfNotepad(BasicNotepadEntity condition) {
		R<IPage<BasicNotepadEntity>> json = new R<>();
		WebUser user = UserUtil.getUser();
		condition.setCreateUserId(user.getUserId());
		IPage<BasicNotepad> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<BasicNotepadEntity> dataList = this.baseMapper.getSelfNotepad(page, condition);
		if (dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}

	/**
	 * 保存事件信息（只记录每日小记，代办放到对应代办功能中）
	 * 
	 * @param notepad 事件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.02.10
	 */
	public R<Object> saveNotepad(BasicNotepad notepad) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		LocalDateTime curDateTime = LocalDateTime.now();
		notepad.setCreateTime(curDateTime);
		if (!StringUtils.hasText(notepad.getCreateDate())) {
			notepad.setCreateDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
		} else {
			if (!ParamVerify.verifyDateByStr(notepad.getCreateDate())) {
				json.setResultEnum(ResultEnum.PARAMETER_ERROR);
				json.setMessage(DATEFORMATTER_ERROR_MESSAGE);
				return json;
			}
		}
		notepad.setCreateUserId(user.getUserId());
		notepad.setUpdateUserId(user.getUserId());
		notepad.setUpdateTime(curDateTime);
		notepad.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
		notepad.setType("1");
		this.baseMapper.insert(notepad);

		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 更新事件信息
	 * 注：只能修改自己记录的事件
	 * @param notepad 事件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.02.10
	 */
	public R<Object> updateNotepad(BasicNotepad notepad) {
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		if(notepad.getNoteId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请先选择要修改的消息");
			return json;
		}
		LambdaUpdateWrapper<BasicNotepad> update = new LambdaUpdateWrapper<>();
		update.eq(BasicNotepad::getNoteId, notepad.getNoteId());
		update.eq(BasicNotepad::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.eq(BasicNotepad::getCreateUserId, user.getUserId());
		update.set(BasicNotepad::getTitle, notepad.getTitle());
		update.set(BasicNotepad::getNoteMessage, notepad.getNoteMessage());
		update.set(BasicNotepad::getUpdateTime, LocalDateTime.now());
		if (StringUtils.hasText(notepad.getCreateDate())) {
			if(ParamVerify.verifyDateByStr(notepad.getCreateDate())) {
				update.set(BasicNotepad::getCreateDate, notepad.getCreateDate());
			}else {
				throw new CustomException(DATEFORMATTER_ERROR_MESSAGE);
			}
		} 
		int updateRow = this.baseMapper.update(null, update);
		if(updateRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
		}else {
			json.setResultEnum(ResultEnum.SUCCESS);
		}
		return json;
	}
	
	/**
	 * 删除事件信息
	 * 注：只能修改自己记录的事件
	 * @param notepad 事件信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.02.10
	 */
	public R<Object> deleteNotepad(BasicNotepad notepad){
		R<Object> json = new R<>();
		WebUser user = UserUtil.getUser();
		if(notepad.getNoteId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			json.setMessage("请先选择要删除的消息");
			return json;
		}
		LambdaUpdateWrapper<BasicNotepad> update = new LambdaUpdateWrapper<>();
		update.eq(BasicNotepad::getNoteId, notepad.getNoteId());
		update.eq(BasicNotepad::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.eq(BasicNotepad::getCreateUserId, user.getUserId());
		update.set(BasicNotepad::getUpdateTime, LocalDateTime.now());
		update.set(BasicNotepad::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		int delRow = this.baseMapper.update(null, update);
		if(delRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
		}else {
			json.setResultEnum(ResultEnum.SUCCESS);
		}
		return json;
	}
	
	/**
	 * 获取一个月内每日的消息数量
	 * @param condition 查询条件
	 * @return
	 *
	 * @author zhouhui
	 * @since 2023.02.11
	 */
	public R<List<Map<String, Integer>>> getDailyNotepadOfMonth(BasicNotepadEntity condition){
		R<List<Map<String, Integer>>> json = new R<>();
		WebUser user = UserUtil.getUser();
		condition.setCreateUserId(user.getUserId());
		if(StringUtils.hasText(condition.getMonth())) {
			YearMonth curMonth = YearMonth.parse(condition.getMonth(), DateTimeFormatter.ofPattern("yyyy-MM"));
			condition.setStartDate(curMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
			condition.setEndDate(curMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE));
		}
		List<Map<String, Integer>> dataList = this.baseMapper.getDailyNotepadOfMonth(condition);
		if(dataList == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}
	
	/**
	 * 根据消息的唯一标识获取消息信息
	 * @param condition 消息的ID
	 * @return BasicNotepadEntity 消息详细内容
	 *
	 * @author zhouhui
	 * @since 2023.02.14
	 */
	public R<BasicNotepad> getNotepadById(BasicNotepadEntity condition){
		R<BasicNotepad> json = new R<>();
		if(condition.getNoteId() == null) {
			json.setResultEnum(ResultEnum.PARAMETER_ERROR);
			return json;
		}
		WebUser user = UserUtil.getUser();
		LambdaQueryWrapper<BasicNotepad> query = new LambdaQueryWrapper<>();
		query.eq(BasicNotepad::getNoteId, condition.getNoteId());
		query.eq(BasicNotepad::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		query.eq(BasicNotepad::getCreateUserId, user.getUserId());
		List<BasicNotepad> noteList = this.baseMapper.selectList(query);
		if(noteList == null || noteList.isEmpty()) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setData(noteList.get(0));
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}
}
