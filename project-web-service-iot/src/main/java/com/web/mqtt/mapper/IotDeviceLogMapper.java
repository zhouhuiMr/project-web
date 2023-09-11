package com.web.mqtt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.iot.mqtt.IotDeviceLog;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 设备日志 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Mapper
public interface IotDeviceLogMapper extends BaseMapper<IotDeviceLog> {

}
