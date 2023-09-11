package com.web.common.iot.mqtt.api;

import org.springframework.cloud.openfeign.FeignClient;
import com.web.common.api.config.ServiceInstanceName;

@FeignClient(name = ServiceInstanceName.SERVICE_IOT_NAME, contextId = "iotDeviceLogApi", path = "/iot/deviceLog")
public interface IotDeviceLogApi {

}
