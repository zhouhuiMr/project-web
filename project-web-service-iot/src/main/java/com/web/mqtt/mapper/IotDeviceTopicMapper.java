package com.web.mqtt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.iot.mqtt.IotDeviceTopic;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 设备对应的消息主题 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Mapper
public interface IotDeviceTopicMapper extends BaseMapper<IotDeviceTopic> {

}
