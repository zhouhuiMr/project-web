package com.web.mqtt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.iot.mqtt.IotDevice;
import com.web.common.iot.mqtt.IotDeviceEntity;
import com.web.common.iot.mqtt.IotDeviceExport;
import com.web.common.iot.mqtt.IotDeviceTopic;
import com.web.common.iot.mqtt.IotDeviceTopicEntity;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.iot.mqtt.api.IotDeviceApi;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.mqtt.service.impl.IotDeviceServiceImpl;
import com.web.service.filter.resubmit.Resubmit;
import com.web.service.handler.excel.ExportExcel;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 设备信息 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Tag(name = "MQTT")
@Controller
@RequestMapping("/iot/device")
public class IotDeviceController implements IotDeviceApi {
	
	@Autowired
	private IotDeviceServiceImpl iotDeviceServiceImpl;

	@Operation(summary = "获取设备列表")
	@Override
	public R<IPage<IotDeviceEntity>> getDeviceList(IotDeviceEntity condition) {
		return iotDeviceServiceImpl.getDeviceList(condition);
	}
	
	@Operation(summary = "导出设备列表")
	@ExportExcel(sheetName = "设备信息", fileName = "设备信息")
	@Override
	public List<IotDeviceExport> getDeviceExportList(IotDeviceEntity condition) {
		return iotDeviceServiceImpl.getDeviceExportList(condition);
	}

	@Resubmit
	@Operation(summary = "保存设备信息")
	@Override
	public R<Object> saveDevice(@Valid IotDevice device) {
		return iotDeviceServiceImpl.saveDevice(device);
	}

	@Resubmit
	@Operation(summary = "更新设备信息")
	@Override
	public R<Object> updateDevice(@Valid IotDevice device) {
		R<Object> json = new R<>();
		if(device.getDeviceId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择需要更新的设备");
			return json;
		}
		return iotDeviceServiceImpl.updateDevice(device);
	}

	@Resubmit
	@Operation(summary = "删除设备信息")
	@Override
	public R<Object> deleteDevice(IotDevice device) {
		R<Object> json = new R<>();
		if(device.getDeviceId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择需要删除的设备");
			return json;
		}
		return iotDeviceServiceImpl.deleteDevice(device);
	}

	@Resubmit
	@Operation(summary = "重新设置设备连接的key")
	@Override
	public R<Object> changeDeviceKey(IotDevice device) {
		R<Object> json = new R<>();
		if(device.getDeviceId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择需要更新key的设备");
			return json;
		}
		return iotDeviceServiceImpl.changeDeviceKey(device);
	}

	@Resubmit
	@Operation(summary = "重新设置设备连接的secret")
	@Override
	public R<Object> changeDeviceSecret(IotDevice device) {
		R<Object> json = new R<>();
		if(device.getDeviceId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择需要更新secret的设备");
			return json;
		}
		return iotDeviceServiceImpl.changeDeviceSecret(device);
	}

	@Operation(summary = "保存设备对应的消息主题")
	@Override
	public R<Object> saveDeviceTopic(@Valid IotDeviceTopic deviceTopic) {
		return iotDeviceServiceImpl.saveDeviceTopic(deviceTopic);
	}

	@Operation(summary = "根据消息主题获取所有的设备信息")
	@Override
	public R<List<IotDevice>> getDeviceByTopic(IotDeviceTopicEntity deviceTopic) {
		R<List<IotDevice>> json = new R<>();
		if(!StringUtils.hasText(deviceTopic.getTopicName())) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择消息主题名称");
			return json;
		}
		return iotDeviceServiceImpl.getDeviceByTopic(deviceTopic);
	}

	@Operation(summary = "根据设备信息获取所有的消息主题")
	@Override
	public R<List<IotMqttTopic>> getTopicByDevice(IotDeviceTopicEntity deviceTopic) {
		R<List<IotMqttTopic>> json = new R<>();
		if(!StringUtils.hasText(deviceTopic.getDeviceName())) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择设备名称");
			return json;
		}
		return iotDeviceServiceImpl.getTopicByDevice(deviceTopic);
	}

	@Operation(summary = "根据设备信息获取所有消息主题的对应关系")
	@Override
	public R<IPage<IotDeviceTopicEntity>> getAllTopicByDevice(IotDeviceTopicEntity deviceTopic) {
		R<IPage<IotDeviceTopicEntity>> json = new R<>();
		if(deviceTopic.getDeviceId() == null) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择设备信息");
			return json;
		}
		return iotDeviceServiceImpl.getAllTopicByDevice(deviceTopic);
	}
}
