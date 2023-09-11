package com.web.common.iot.mqtt;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ColumnWidth(value = 25)
public class IotDeviceExport {

	/** 设备名称 */
	@ExcelProperty(value = "设备名称")
	private String deviceName;
	
	/** 设备状态 */
	@ExcelProperty(value = "设备状态")
	private String deviceStatusName;
	
	/** 创建时间 */
	@ExcelProperty(value = "创建时间")
	private String createTime;
}
