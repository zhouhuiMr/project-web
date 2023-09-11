package com.web.mqtt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.iot.mqtt.IotMqttTopicEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * mqtt消息主题 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Mapper
public interface IotMqttTopicMapper extends BaseMapper<IotMqttTopic> {

	/**
	 * 获取主题信息列表
	 * @param page 分页信息
	 * @param condition 查询条件
	 * @return IPage<IotMqttTopicEntity> 
	 *
	 * @author zhouhui
	 * @since 2023.06.20
	 */
	IPage<IotMqttTopicEntity> getTopicList(Page<IotMqttTopic> page, @Param("condition") IotMqttTopicEntity condition);
}
