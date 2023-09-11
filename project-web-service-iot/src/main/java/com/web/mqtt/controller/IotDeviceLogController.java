package com.web.mqtt.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.web.common.iot.mqtt.api.IotDeviceLogApi;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 设备日志 前端控制器
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Tag(name = "MQTT")
@Controller
@RequestMapping("/iot/deviceLog")
public class IotDeviceLogController implements IotDeviceLogApi{

}
