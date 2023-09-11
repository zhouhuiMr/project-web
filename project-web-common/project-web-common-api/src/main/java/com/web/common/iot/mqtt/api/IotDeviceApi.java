package com.web.common.iot.mqtt.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.iot.mqtt.IotDevice;
import com.web.common.iot.mqtt.IotDeviceEntity;
import com.web.common.iot.mqtt.IotDeviceExport;
import com.web.common.iot.mqtt.IotDeviceTopic;
import com.web.common.iot.mqtt.IotDeviceTopicEntity;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.result.R;

@FeignClient(name = ServiceInstanceName.SERVICE_IOT_NAME, contextId = "iotDeviceApi", path = "/iot/device")
public interface IotDeviceApi {

	/**
	 * 获取设备列表
	 * @param condition 查询条件
	 * @return IPage<IotDeviceEntity> 分页列表
	 *
	 * @author zhouhui
	 * @since 2023.06.13
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<IotDeviceEntity>> getDeviceList(@RequestBody IotDeviceEntity condition);
	
	/**
	 * 获取设备列表导出
	 * @param condition 查询条件
	 * @return List<IotDeviceExport> 导出数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.14 
	 */
	@PostMapping(path = "/list/export")
	List<IotDeviceExport> getDeviceExportList(@RequestBody IotDeviceEntity condition);
	
	/**
	 * 保存设备信息
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.13
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<Object> saveDevice(@RequestBody @Valid IotDevice device);
	
	/**
	 * 更新设备信息
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.13
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<Object> updateDevice(@RequestBody @Valid IotDevice device);
	
	/**
	 * 删除设备信息
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.13
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> deleteDevice(@RequestBody IotDevice device);
	
	/**
	 * 更新设备的Key
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14 
	 */
	@PostMapping(path = "/changeDeviceKey")
	@ResponseBody
	R<Object> changeDeviceKey(@RequestBody IotDevice device);
	
	/**
	 * 更新设备的secret
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14 
	 */
	@PostMapping(path = "/changeDeviceSecret")
	@ResponseBody
	R<Object> changeDeviceSecret(@RequestBody IotDevice device);
	
	/**
	 * 保存设备对应的消息主题
	 * @param deviceTopic 设备与消息主题对应关系
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	@PostMapping(path = "/saveDeviceTopic")
	@ResponseBody
	R<Object> saveDeviceTopic(@RequestBody @Valid IotDeviceTopic deviceTopic);
	
	/**
	 * 根据消息主题获取所有的设备信息
	 * @param deviceTopic 消息主题
	 * @return List<IotDevice> 设备列表
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	@PostMapping(path = "/getDeviceByTopic")
	@ResponseBody
	R<List<IotDevice>> getDeviceByTopic(@RequestBody IotDeviceTopicEntity deviceTopic);
	
	/**
	 * 根据设备信息获取所有的消息主题
	 * @param deviceTopic 设备信息
	 * @return List<IotMqttTopic> 所有的主题消息
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	@PostMapping(path = "/getTopicByDevice")
	@ResponseBody
	R<List<IotMqttTopic>> getTopicByDevice(@RequestBody IotDeviceTopicEntity deviceTopic);
	
	/**
	 * 根据设备信息获取所有消息主题的对应关系
	 * @param deviceTopic 设备信息
	 * @return R<IPage<IotDeviceTopicEntity>> 分页数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.22
	 */
	@PostMapping(path = "/getAllTopicByDevice")
	@ResponseBody
	R<IPage<IotDeviceTopicEntity>> getAllTopicByDevice(@RequestBody IotDeviceTopicEntity deviceTopic);
}
