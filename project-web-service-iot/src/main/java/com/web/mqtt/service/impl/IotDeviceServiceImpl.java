package com.web.mqtt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.common.datasource.DataSourcesSymbol;
import com.web.common.iot.mqtt.IotDevice;
import com.web.common.iot.mqtt.IotDeviceEntity;
import com.web.common.iot.mqtt.IotDeviceExport;
import com.web.common.iot.mqtt.IotDeviceTopic;
import com.web.common.iot.mqtt.IotDeviceTopicEntity;
import com.web.common.iot.mqtt.IotMqttTopic;
import com.web.common.password.PasswordEmitter;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;
import com.web.common.user.entity.WebUser;
import com.web.mqtt.mapper.IotDeviceMapper;
import com.web.mqtt.mapper.IotDeviceTopicMapper;
import com.web.mqtt.mapper.IotMqttTopicMapper;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 设备信息 服务实现类
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> {

	/** key的长度（8） */
	private static final int DEVICE_KEY_LENGTH = 8;

	/** secret的长度（16） */
	private static final int DEVICE_SECRET_LENGTH = 16;

	@Autowired
	private IotMqttTopicMapper iotMqttTopicMapper;

	@Autowired
	private IotDeviceTopicMapper iotDeviceTopicMapper;

	/**
	 * 获取设备数据列表
	 * 
	 * @param condition 查询条件
	 * @return R<IPage<IotDeviceEntity>> 数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public R<IPage<IotDeviceEntity>> getDeviceList(IotDeviceEntity condition) {
		R<IPage<IotDeviceEntity>> json = new R<>();
		setSearchCondition(condition);
		Page<IotDevice> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<IotDeviceEntity> result = baseMapper.getDeviceList(page, condition);
		if (result == null) {
			json.setResultEnum(ResultEnum.DATA_QUERY_ERROR);
			return json;
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(result);
		return json;
	}

	/**
	 * 导出设备数据列表
	 * 
	 * @param condition 查询条件
	 * @return List<IotDeviceExport> 数据列表
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public List<IotDeviceExport> getDeviceExportList(IotDeviceEntity condition) {
		setSearchCondition(condition);
		Page<IotDevice> page = new Page<>(1, -1);
		IPage<IotDeviceEntity> result = baseMapper.getDeviceList(page, condition);
		if (result == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		List<IotDeviceEntity> dataList = result.getRecords();
		List<IotDeviceExport> export = new ArrayList<>(dataList.size());
		for (IotDeviceEntity item : dataList) {
			IotDeviceExport exportItem = new IotDeviceExport();
			BeanUtils.copyProperties(item, exportItem);
			exportItem.setCreateTime(
					item.getCreateTime().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE_TIME)));
			export.add(exportItem);
		}
		return export;
	}

	/**
	 * 保存设备信息。 1、判断是否存在相同的设备名称，如果不存在相同的设备名称则进行新增； 2、保存设备信息。
	 * 
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public R<Object> saveDevice(IotDevice device) {
		R<Object> json = new R<>();

		LambdaQueryWrapper<IotDevice> query = new LambdaQueryWrapper<>();
		query.eq(IotDevice::getDeviceName, device.getDeviceName());
		query.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<IotDevice> dataList = this.baseMapper.selectList(query);

		if (dataList != null && dataList.isEmpty()) {
			WebUser user = UserUtil.getUser();
			device.setCreateUserId(user.getUserId());
			device.setCreateTime(LocalDateTime.now());
			device.setDeviceKey(PasswordEmitter.getPassword(DEVICE_KEY_LENGTH));
			device.setDeviceSecret(PasswordEmitter.getPassword(DEVICE_SECRET_LENGTH));
			device.setDeviceStatus(DataSourcesSymbol.DEVICE_STATUS_0);
			device.setDelFlag(DataSourcesSymbol.DEL_FLAG_VALUE_0);
			int saveRow = this.baseMapper.insert(device);
			if (saveRow > 0) {
				json.setResultEnum(ResultEnum.SUCCESS);
			} else {
				json.setResultEnum(ResultEnum.DATA_INSERT_ERROR);
			}
		} else {
			throw new CustomException(ResultEnum.DATA_QUERY_SAME.getMessage());
		}

		return json;
	}

	/**
	 * 更新设备信息。 1、判断除自身之外是否存在相同的设备名称； 2、如果没有相同的设备名称，则进行数据更新。
	 * 
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public R<Object> updateDevice(IotDevice device) {
		R<Object> json = new R<>();

		LambdaQueryWrapper<IotDevice> query = new LambdaQueryWrapper<>();
		query.eq(IotDevice::getDeviceName, device.getDeviceName());
		query.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		query.ne(IotDevice::getDeviceId, device.getDeviceId());
		List<IotDevice> dataList = this.baseMapper.selectList(query);

		if (dataList != null && dataList.isEmpty()) {
			WebUser user = UserUtil.getUser();
			device.setUpdateTime(LocalDateTime.now());
			device.setUpdateUserId(user.getUserId());
			device.setDeviceKey(null);
			device.setDeviceSecret(null);
			device.setDeviceStatus(null);
			LambdaUpdateWrapper<IotDevice> update = new LambdaUpdateWrapper<>();
			update.eq(IotDevice::getDeviceId, device.getDeviceId());
			update.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
			int updateRow = this.baseMapper.update(device, update);
			if (updateRow > 0) {
				json.setResultEnum(ResultEnum.SUCCESS);
			} else {
				json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
			}
		} else {
			throw new CustomException(ResultEnum.DATA_QUERY_SAME.getMessage());
		}
		return json;
	}

	/**
	 * 删除设备信息。
	 * 
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public R<Object> deleteDevice(IotDevice device) {
		R<Object> json = new R<>();

		WebUser user = UserUtil.getUser();

		LambdaUpdateWrapper<IotDevice> update = new LambdaUpdateWrapper<>();
		update.eq(IotDevice::getDeviceId, device.getDeviceId());
		update.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(IotDevice::getUpdateTime, LocalDateTime.now());
		update.set(IotDevice::getUpdateUserId, user.getUserId());
		update.set(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_1);
		int deleteRow = this.baseMapper.update(null, update);
		if (deleteRow <= 0) {
			json.setResultEnum(ResultEnum.DATA_DELETE_ERROR);
		} else {
			json.setResultEnum(ResultEnum.SUCCESS);
		}
		return json;
	}

	/**
	 * 设置查询条件
	 * 
	 * @param condition 查询条件
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	private void setSearchCondition(IotDeviceEntity condition) {
		if (StringUtils.hasText(condition.getDeviceName())) {
			condition.setDeviceName("%" + condition.getDeviceName() + "%");
		}
	}

	/**
	 * 更新设备对应的key信息
	 * 
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public R<Object> changeDeviceKey(IotDevice device) {
		R<Object> json = new R<>();

		WebUser user = UserUtil.getUser();

		LambdaUpdateWrapper<IotDevice> update = new LambdaUpdateWrapper<>();
		update.eq(IotDevice::getDeviceId, device.getDeviceId());
		update.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(IotDevice::getUpdateTime, LocalDateTime.now());
		update.set(IotDevice::getUpdateUserId, user.getUserId());
		update.set(IotDevice::getDeviceKey, PasswordEmitter.getPassword(DEVICE_KEY_LENGTH));
		int updateRow = this.baseMapper.update(null, update);
		if (updateRow > 0) {
			json.setResultEnum(ResultEnum.SUCCESS);
		} else {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
		}
		return json;
	}

	/**
	 * 更新设备对应的secret信息
	 * 
	 * @param device 设备信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.14
	 */
	public R<Object> changeDeviceSecret(IotDevice device) {
		R<Object> json = new R<>();

		WebUser user = UserUtil.getUser();

		LambdaUpdateWrapper<IotDevice> update = new LambdaUpdateWrapper<>();
		update.eq(IotDevice::getDeviceId, device.getDeviceId());
		update.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		update.set(IotDevice::getUpdateTime, LocalDateTime.now());
		update.set(IotDevice::getUpdateUserId, user.getUserId());
		update.set(IotDevice::getDeviceSecret, PasswordEmitter.getPassword(DEVICE_SECRET_LENGTH));
		int updateRow = this.baseMapper.update(null, update);
		if (updateRow > 0) {
			json.setResultEnum(ResultEnum.SUCCESS);
		} else {
			json.setResultEnum(ResultEnum.DATA_UPDATE_ERROR);
		}
		return json;
	}

	/**
	 * 保存或者更新设备对应的消息主题。<br>
	 * 1、判断设备和消息主题是否存在；<br>
	 * 2、根据设备和消息主题关联关系是否存在；<br>
	 * 3、如果数据存在则进行更新；<br>
	 * 4、如果数据不存在则进行保存。
	 * 
	 * @param deviceTopic 设备对应消息主题
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	public R<Object> saveDeviceTopic(IotDeviceTopic deviceTopic) {
		R<Object> json = new R<>();
		if (!deviceIsExist(deviceTopic)) {
			throw new CustomException("设备信息不存在");
		}
		if (!topicIsExist(deviceTopic)) {
			throw new CustomException("消息主题不存在");
		}
		if (deviceTopic.getStatus() == null || (deviceTopic.getStatus() != 1 && deviceTopic.getStatus() != 0)) {
			throw new CustomException("未设置设备和消息主题的状态");
		}

		WebUser loginUser = UserUtil.getUser();
		// 判断对应关系是否存在
		LambdaUpdateWrapper<IotDeviceTopic> update = new LambdaUpdateWrapper<>();
		update.eq(IotDeviceTopic::getDeviceId, deviceTopic.getDeviceId());
		update.eq(IotDeviceTopic::getTopicId, deviceTopic.getTopicId());
		update.set(IotDeviceTopic::getStatus, deviceTopic.getStatus());
		update.set(IotDeviceTopic::getUpdateUserId, loginUser.getUserId());
		update.set(IotDeviceTopic::getUpdateTime, LocalDateTime.now());
		int updateRow = iotDeviceTopicMapper.update(null, update);
		if (updateRow <= 0) {
			// 新增数据
			deviceTopic.setCreateUserId(loginUser.getUserId());
			deviceTopic.setCreateTime(LocalDateTime.now());
			iotDeviceTopicMapper.insert(deviceTopic);
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 判断设备是否存在。
	 * 
	 * @param deviceTopic 设备信息
	 * @return boolean true存在；false不存在
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	private boolean deviceIsExist(IotDeviceTopic deviceTopic) {
		LambdaQueryWrapper<IotDevice> query = new LambdaQueryWrapper<>();
		query.eq(IotDevice::getDeviceId, deviceTopic.getDeviceId());
		query.eq(IotDevice::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<IotDevice> deviceList = this.baseMapper.selectList(query);
		return !(deviceList == null || deviceList.isEmpty());
	}

	/**
	 * 消息主题是否存在。
	 * 
	 * @param deviceTopic 消息主题
	 * @return boolean true存在；false不存在。
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	private boolean topicIsExist(IotDeviceTopic deviceTopic) {
		LambdaQueryWrapper<IotMqttTopic> query = new LambdaQueryWrapper<>();
		query.eq(IotMqttTopic::getTopicId, deviceTopic.getTopicId());
		query.eq(IotMqttTopic::getDelFlag, DataSourcesSymbol.DEL_FLAG_VALUE_0);
		List<IotMqttTopic> topicList = iotMqttTopicMapper.selectList(query);
		return !(topicList == null || topicList.isEmpty());
	}

	/**
	 * 根据消息主题的名称获取所有对应的设备
	 * 
	 * @param deviceTopic 消息主题内容
	 * @return R<List<IotDevice>> 设备列表
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	public R<List<IotDevice>> getDeviceByTopic(IotDeviceTopicEntity deviceTopic) {
		R<List<IotDevice>> json = new R<>();
		List<IotDevice> dataList = this.baseMapper.getDeviceByTopic(deviceTopic);
		if (dataList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		json.setData(dataList);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 根据设备信息获取消息主题。
	 * 
	 * @param deviceTopic 设备信息
	 * @return List<IotMqttTopic>主题信息
	 *
	 * @author zhouhui
	 * @since 2023.06.23
	 */
	public R<List<IotMqttTopic>> getTopicByDevice(IotDeviceTopicEntity deviceTopic) {
		R<List<IotMqttTopic>> json = new R<>();
		List<IotMqttTopic> dataList = this.baseMapper.getTopicByDevice(deviceTopic);
		if (dataList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		json.setData(dataList);
		json.setResultEnum(ResultEnum.SUCCESS);
		return json;
	}

	/**
	 * 根据设备编号获取对应的所有消息主题的状态。<br>
	 * 1、判断设备是否存在；<br>
	 * 2、获取设备对应的消息主题状态。
	 * @param condition 查询条件
	 * @return IPage<IotDeviceTopicEntity> 分页信息列表
	 *
	 * @author zhouhui
	 * @since 2023.06.24
	 */
	public R<IPage<IotDeviceTopicEntity>> getAllTopicByDevice(IotDeviceTopicEntity condition) {
		R<IPage<IotDeviceTopicEntity>> json = new R<>();
		if(!deviceIsExist(condition)) {
			throw new CustomException("没有对应的设备信息");
		}
		IPage<IotDeviceTopic> page = new Page<>(condition.getPage(), condition.getSize());
		IPage<IotDeviceTopicEntity> dataList = this.baseMapper.getAllTopicByDevice(page, condition);
		if (dataList == null) {
			throw new CustomException(ResultEnum.DATA_QUERY_ERROR.getMessage());
		}
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataList);
		return json;
	}
}
