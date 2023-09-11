package com.web.common.iot.mqtt.api;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.iot.mqtt.IotMqttTopicEntity;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_IOT_NAME, contextId = "iotMqttTopicApi", path = "/iot/mqtt/topic")
public interface IotMqttTopicApi {

	/**
	 * 获取主题列表
	 * @param condition 查询条件
	 * @return R<IPage<IotMqttTopicEntity>> 数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.19
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<IotMqttTopicEntity>> getTopicList(@RequestBody IotMqttTopicEntity condition);
	
	/**
	 * 保存主题内容
	 * @param topic 主题内容
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.19
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<Object> saveTopic(@RequestBody @Valid IotMqttTopic topic);
	
	/**
	 * 更新主题信息
	 * @param topic 主题信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.19
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<Object> updateTopic(@RequestBody @Valid IotMqttTopic topic);
	
	/**
	 * 删除主题信息
	 * @param topic 主题信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.19
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> deleteTopic(@RequestBody IotMqttTopic topic);
}
