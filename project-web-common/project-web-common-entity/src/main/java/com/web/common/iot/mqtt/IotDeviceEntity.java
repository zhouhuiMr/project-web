package com.web.common.iot.mqtt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 设备信息
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Getter
@Setter
@Schema(title = "IotDeviceEntity对象", description = "设备信息")
public class IotDeviceEntity extends IotDevice{

    private static final long serialVersionUID = 1L;
    
    /** 设备状态描述 */
	@Schema(title = "设备状态描述")
    private String deviceStatusName;
    
    /** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
