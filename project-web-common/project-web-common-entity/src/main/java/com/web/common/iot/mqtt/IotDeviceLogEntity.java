package com.web.common.iot.mqtt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 设备日志
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Getter
@Setter
@Schema(title = "IotDeviceLogEntity对象", description = "设备日志")
public class IotDeviceLogEntity extends IotDeviceLog{

    private static final long serialVersionUID = 1L;
}
