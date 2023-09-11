package com.web.common.iot.mqtt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 设备对应的消息主题
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Getter
@Setter
@Schema(title = "IotDeviceTopicEntity对象", description = "设备对应的消息主题")
public class IotDeviceTopicEntity extends IotDeviceTopic{

    private static final long serialVersionUID = 1L;
    
    /** 设备名称 */
    @Schema(title = "设备名称")
    private String deviceName;
    
    /** 消息主题名称 */
    @Schema(title = "消息主题名称")
    private String topicName;
    
    /** 消息主题的QoS */
    @Schema(title = "消息主题的QoS")
    private String topicQos;
    
    /** 消息主题的QoS描述 */
    @Schema(title = "消息主题的QoS描述")
    private String topicQosName;
    
    /** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
