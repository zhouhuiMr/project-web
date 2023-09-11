package com.web.common.iot.mqtt;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("iot_device_log")
@Schema(title = "IotDeviceLog对象", description = "设备日志")
public class IotDeviceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 设备id */
    @Schema(title = "设备id")
    private Integer deviceId;

    /** 经度 */
    @Schema(title = "经度")
    private String lng;

    /** 纬度 */
    @Schema(title = "纬度")
    private String lat;

    /** 设备在线状态，1离线；2在线 */
    @Schema(title = "设备在线状态，1离线；2在线")
    private Integer deviceStatus;

    /** 创建年份，格式yyyy */
    @Schema(title = "创建年份，格式yyyy")
    private Integer createYear;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;


}
