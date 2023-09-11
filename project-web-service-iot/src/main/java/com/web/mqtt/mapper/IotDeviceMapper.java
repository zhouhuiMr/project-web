package com.web.mqtt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.iot.mqtt.IotDevice;
import com.web.common.iot.mqtt.IotDeviceEntity;
import com.web.common.iot.mqtt.IotDeviceTopic;
import com.web.common.iot.mqtt.IotDeviceTopicEntity;
import com.web.common.iot.mqtt.IotMqttTopic;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备信息 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Mapper
public interface IotDeviceMapper extends BaseMapper<IotDevice> {

	/**
	 * 分页获取设备列表
	 * @param condition 查询条件
	 * @param page 分页信息
	 * @return IPage<IotDeviceEntity> 数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.14 
	 */
	IPage<IotDeviceEntity> getDeviceList(Page<IotDevice> page, @Param("condition") IotDeviceEntity condition);

	/**
	 * 根据消息主题的名称获取所有对应的设备
	 * @param condition 查询条件
	 * @return List<IotDevice> 设备列表
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	List<IotDevice> getDeviceByTopic(@Param("condition") IotDeviceTopicEntity condition);
	
	/**
	 * 根据设备名称获取消息主题
	 * @param condition 查询条件
	 * @return List<IotMqttTopic> 消息主题列表
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	List<IotMqttTopic> getTopicByDevice(@Param("condition") IotDeviceTopicEntity condition);
	
	/**
	 * 根据设备信息获取所有的消息主题状态
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<IotDeviceTopicEntity> 分页数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.24
	 */
	IPage<IotDeviceTopicEntity> getAllTopicByDevice(IPage<IotDeviceTopic> page, @Param("condition") IotDeviceTopicEntity condition);
}
