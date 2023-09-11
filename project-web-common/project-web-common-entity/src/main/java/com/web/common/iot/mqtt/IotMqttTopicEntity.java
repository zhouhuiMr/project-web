package com.web.common.iot.mqtt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * mqtt消息主题
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Getter
@Setter
@Schema(title = "IotMqttTopicEntity对象", description = "mqtt消息主题")
public class IotMqttTopicEntity extends IotMqttTopic{

    private static final long serialVersionUID = 1L;
    
    /** QoS描述 */
	@Schema(title = "QoS描述")
    private String qosName;
    
    /** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
