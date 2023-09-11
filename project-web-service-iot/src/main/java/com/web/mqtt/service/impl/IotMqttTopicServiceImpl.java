package com.web.mqtt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.iot.mqtt.IotMqttTopicEntity;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.mqtt.mapper.IotMqttTopicMapper;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * mqtt消息主题 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Service
public class IotMqttTopicServiceImpl extends ServiceImpl<IotMqttTopicMapper, IotMqttTopic> {

	public R<IPage<IotMqttTopicEntity>> getTopicList(IotMqttTopicEntity condition) {
		R<IPage<IotMqttTopicEntity>> json = new R<>();
		setSearchCondition(condition);
		return json;
	}

	/**
	 * 设置查询条件
	 * 
	 * @param condition 查询条件
	 *
	 * @author zhouhui
	 * @since 2023.06.20
	 */
	private void setSearchCondition(IotMqttTopicEntity condition) {
		if (StringUtils.hasText(condition.getTopicName())) {
			condition.setTopicName("%" + condition.getTopicName() + "%");
		}
	}

	/**
	 * 保存主题信息。<br>
	 * 1、判断是否存在相同主题名称数据，如果存在则返回异常；<br>
	 * 2、保存数据。
	 * 
	 * @param topic 主题信息
	 * @return R<Object>处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	public R<Object> saveTopic(IotMqttTopic topic) {
		R<Object> json = new R<>();
		if (!checkQos(topic.getTopicQos())) {
			throw new CustomException("设置QoS错误");
		}
		// 判断是否存在相同的topic
		LambdaQueryWrapper<IotMqttTopic> query = new LambdaQueryWrapper<>();
		query.eq(IotMqttTopic::getTopicName, topic.getTopicName());
		query.eq(IotMqttTopic::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<IotMqttTopic> topicList = baseMapper.selectList(query);
		if (topicList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		if (!topicList.isEmpty()) {
			throw new CustomException(ResultEnum.DATA_QUERY_SAME.getMessage());
		}
		// 保存数据
		WebUser loginUser = UserUtil.getUser();
		topic.setCreateUserId(loginUser.getUserId());
		topic.setCreateTime(LocalDateTime.now());
		topic.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
		int saveRow = this.baseMapper.insert(topic);
		if(saveRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_INSERT_ERROR);
		}else {
			json.setResultEnum(ResultEnum.SUCCESS);
		}
		return json;
	}

	/**
	 * 更新主题信息。<br>
	 * 1、判断是否存在相同主题名称数据，如果存在则返回异常；<br>
	 * 2、更新数据。
	 * 
	 * @param topic 主题信息
	 * @return R<Object>处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	public R<Object> updateTopic(IotMqttTopic topic) {
		R<Object> json = new R<>();
		if (!checkQos(topic.getTopicQos())) {
			throw new CustomException("设置QoS错误");
		}
		// 判断是否存在相同的topic
		LambdaQueryWrapper<IotMqttTopic> query = new LambdaQueryWrapper<>();
		query.eq(IotMqttTopic::getTopicName, topic.getTopicName());
		query.eq(IotMqttTopic::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		query.ne(IotMqttTopic::getTopicId, topic.getTopicId());
		List<IotMqttTopic> topicList = baseMapper.selectList(query);
		if (topicList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		if (!topicList.isEmpty()) {
			throw new CustomException(ResultEnum.DATA_QUERY_SAME.getMessage());
		}
		// 更新数据
		topic.setCreateUserId(null);
		topic.setCreateTime(null);
		topic.setDelFlag(null);

		WebUser loginUser = UserUtil.getUser();

		LambdaUpdateWrapper<IotMqttTopic> update = new LambdaUpdateWrapper<>();
		update.eq(IotMqttTopic::getTopicId, topic.getTopicId());
		update.eq(IotMqttTopic::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(IotMqttTopic::getUpdateUserId, loginUser.getUserId());
		update.set(IotMqttTopic::getUpdateTime, LocalDateTime.now());
		int updateRow = this.baseMapper.update(topic, update);
		if(updateRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
		}else {
			json.setResultEnum(ResultEnum.SUCCESS);
		}
		return json;
	}

	/**
	 * 删除消息主题。
	 * @param topic 需要删除的消息主题信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	public R<Object> deleteTopic(IotMqttTopic topic) {
		R<Object> json = new R<>();
		WebUser loginUser = UserUtil.getUser();
		
		LambdaUpdateWrapper<IotMqttTopic> update = new LambdaUpdateWrapper<>();
		update.eq(IotMqttTopic::getTopicId, topic.getTopicId());
		update.eq(IotMqttTopic::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(IotMqttTopic::getUpdateUserId, loginUser.getUserId());
		update.set(IotMqttTopic::getUpdateTime, LocalDateTime.now());
		update.set(IotMqttTopic::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		int deleteRow = this.baseMapper.update(null, update);
		if(deleteRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
		}else {
			json.setResultEnum(ResultEnum.SUCCESS);
		}
		return json;
	}

	/**
	 * 判断设置的QoS是否正确
	 * 
	 * @param qos 传输的QoS
	 * @return true正确；false错误
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	private boolean checkQos(Integer qos) {
		if (qos == null) {
			return false;
		}
		List<Integer> qosList = new ArrayList<>(3);
		qosList.add(MqttQoS.EXACTLY_ONCE.value());
		qosList.add(MqttQoS.AT_LEAST_ONCE.value());
		qosList.add(MqttQoS.AT_MOST_ONCE.value());
		return qosList.contains(qos);
	}
}
