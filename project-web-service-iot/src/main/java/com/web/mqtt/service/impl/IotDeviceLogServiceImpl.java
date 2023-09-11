package com.web.mqtt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.iot.mqtt.IotDeviceLog;
import com.web.common.result.R;
import com.web.mqtt.mapper.IotDeviceLogMapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备日志 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Service
public class IotDeviceLogServiceImpl extends ServiceImpl<IotDeviceLogMapper, IotDeviceLog> {

	
	/**
	 * 保存设备上线下线日志
	 * @param saveLog 日志信息
	 * @return 
	 *
	 * @author zhouhui
	 * @since 2023.06.08 
	 */
	public R<Object> saveLog(IotDeviceLog saveLog) {
		R<Object> json = new R<>();
		if(saveLog.getCreateTime() == null) {
			LocalDateTime curDateTime = LocalDateTime.now();
			saveLog.setCreateTime(curDateTime);
			saveLog.setCreateYear(curDateTime.getYear());
		}
		this.baseMapper.insert(saveLog);
		return json;
	}
}
