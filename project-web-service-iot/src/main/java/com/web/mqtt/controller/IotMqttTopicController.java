package com.web.mqtt.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.iot.mqtt.IotMqttTopicEntity;
import com.web.common.iot.mqtt.api.IotMqttTopicApi;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.mqtt.service.impl.IotMqttTopicServiceImpl;
import com.web.service.filter.resubmit.Resubmit;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * mqtt消息主题 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Tag(name = "MQTT消息主题")
@Controller
@RequestMapping("/iot/mqtt/topic")
public class IotMqttTopicController implements IotMqttTopicApi {
	
	@Autowired
	private IotMqttTopicServiceImpl iotMqttTopicServiceImpl;

	@Operation(summary = "获取消息主题列表")
	@Override
	public R<IPage<IotMqttTopicEntity>> getTopicList(IotMqttTopicEntity condition) {
		return iotMqttTopicServiceImpl.getTopicList(condition);
	}

	@Operation(summary = "保存消息主题")
	@Resubmit
	@Override
	public R<Object> saveTopic(@Valid IotMqttTopic topic) {
		return iotMqttTopicServiceImpl.saveTopic(topic);
	}

	@Operation(summary = "更新消息主题")
	@Override
	public R<Object> updateTopic(@Valid IotMqttTopic topic) {
		R<Object> json = new R<>();
		if(topic.getTopicId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择要修改的消息主题");
			return json;
		}
		return iotMqttTopicServiceImpl.updateTopic(topic);
	}

	@Operation(summary = "删除消息主题")
	@Override
	public R<Object> deleteTopic(IotMqttTopic topic) {
		R<Object> json = new R<>();
		if(topic.getTopicId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择要删除的消息主题");
			return json;
		}
		return iotMqttTopicServiceImpl.deleteTopic(topic);
	}

}
