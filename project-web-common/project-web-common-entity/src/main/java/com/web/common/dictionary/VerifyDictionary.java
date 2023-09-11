package com.web.common.dictionary;

import java.util.List;

import org.springframework.util.ReflectionUtils;

import com.web.common.basic.entity.BasicSysDict;
import com.web.common.dictionary.VerifyDictField.OperateMode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 对{@link VerifyDictField}标记的字段进行处理
 * <p>
 * 1、判断字典项对应的值是否存在；
 * <p>
 * 2、根据字典表中的value，查询对应的value；
 * <p>
 * 3、根据字典表中的label，查询对应的value。
 * 
 * @author zhouhui
 * @since 1.0.0
 */
public class VerifyDictionary {

	private VerifyDictionary() {}

	/**
	 * 针对对象中添加{@link VerifyDictField}注解的属性进行处理。
	 * @param <T>      数据的类型
	 * @param data     数据
	 * @param dictList 字典列表
	 * @return List<DictionaryError> 错误信息
	 *
	 * @author zhouhui
	 * @since 2022-05-06
	 */
	public static <T> List<DictionaryError> operateDict(T data, List<BasicSysDict> dictList) {
		if (data == null || dictList == null || dictList.isEmpty()) {
			return Collections.emptyList();
		}

		List<DictionaryError> error = new ArrayList<>();
		Class<? extends Object> temp = data.getClass();
		while (temp != null) {
			Field[] fields = temp.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(VerifyDictField.class)) {
					VerifyDictField verifyDict = field.getDeclaredAnnotation(VerifyDictField.class);
					switch (verifyDict.operateMode()) {
					case VERIFY:
						List<DictionaryError> verifyErrorList = verify(field, data, verifyDict, dictList);
						saveError(verifyErrorList, error);
						break;
					case VALUE2LABEL:
						List<DictionaryError> value2LableErrorList = setStoreValue(field, data, verifyDict, dictList);
						saveError(value2LableErrorList, error);
						break;
					case LABEL2VALUE:
						List<DictionaryError> lable2ValueErrorList = setStoreValue(field, data, verifyDict, dictList);
						saveError(lable2ValueErrorList, error);
						break;
					default:
						break;
					}
				}
			}
			temp = temp.getSuperclass();
		}
		return error;
	}
	
	/**
	 * 保存错误信息
	 * @param errorList 错误信息列表
	 * @param error 错误信息
	 *
	 * @author zhouhui
	 * @since 2023.03.05
	 */
	private static void saveError(List<DictionaryError> errorList, List<DictionaryError> error) {
		if (errorList != null && !errorList.isEmpty()) {
			error.addAll(errorList);
		}
	}

	/**
	 * 校验标记字段的value在字典项中是否存在。
	 * 
	 * @param field      属性字段
	 * @param data       数据对象
	 * @param verifyDict 标记字段的属性
	 * @param dictList   字典列表
	 * @return List<DictionaryError> 错误信息列表
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static List<DictionaryError> verify(Field field, Object data, VerifyDictField verifyDict,
			List<BasicSysDict> dictList) {
		List<DictionaryError> error = new ArrayList<>();
		String type = verifyDict.dictType();
		boolean isRight = false;
		try {
			ReflectionUtils.makeAccessible(field);
			Object value = field.get(data);
			if (value == null) {
				if (verifyDict.required()) {
					DictionaryError errorItem = new DictionaryError();
					errorItem.setType(type);
					errorItem.setFieldName(field.getName());
					errorItem.setOperateMode(verifyDict.operateMode());
					errorItem.setMessage(field.getName() + "字段没有值");
					error.add(errorItem);
				}
				return error;
			}
			for (BasicSysDict item : dictList) {
				if (type.equals(item.getType()) && value.toString().equals(item.getValue())) {
					isRight = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isRight) {
			DictionaryError errorItem = new DictionaryError();
			errorItem.setType(type);
			errorItem.setFieldName(field.getName());
			errorItem.setOperateMode(verifyDict.operateMode());
			errorItem.setMessage(field.getName() + "字段的值在字典项中不存在");
			error.add(errorItem);
		}
		return error;
	}

	/**
	 * 通过字典数据，将对应的value转成label，或将label转成value。
	 * 
	 * @param field      被注解标记的字段
	 * @param data       对象数据信息
	 * @param verifyDict 被标记字段的注解信息
	 * @param dictList   字典数据信息
	 * @return List<DictionaryError> 错误信息
	 *
	 * @author zhouhui
	 * @since 2022-05-08
	 */
	public static List<DictionaryError> setStoreValue(Field field, Object data, VerifyDictField verifyDict,
			List<BasicSysDict> dictList) {
		List<DictionaryError> error = new ArrayList<>();
		String type = verifyDict.dictType();

		String storeFieldName = verifyDict.storeFieldName();
		if (storeFieldName == null || "".equals(storeFieldName)) {
			DictionaryError errorItem = new DictionaryError();
			errorItem.setType(type);
			errorItem.setFieldName(field.getName());
			errorItem.setOperateMode(verifyDict.operateMode());
			errorItem.setMessage(field.getName() + "未设置存储描述的字段");
			error.add(errorItem);
			return error;
		}

		Object value = null;
		try {
			value = field.get(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (value == null) {
			if (verifyDict.required()) {
				DictionaryError errorItem = new DictionaryError();
				errorItem.setType(type);
				errorItem.setFieldName(field.getName());
				errorItem.setOperateMode(verifyDict.operateMode());
				errorItem.setMessage(field.getName() + "字段没有值");
				error.add(errorItem);
			}
			return error;
		}
		String storeValue = getStoreValue(type, value, verifyDict, dictList);

		setStoreFieldValue(data, storeFieldName, storeValue, error);

		return error;
	}
	
	/**
	 * 获取字典项描述对应的值
	 * @param type 字典类型
	 * @param value 字典项的值
	 * @param verifyDict 属性标记信息
	 * @param dictList 查询到的字典列表
	 * @return String 查询到的描述获取值
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private static String getStoreValue(String type,Object value, VerifyDictField verifyDict, List<BasicSysDict> dictList) {
		String storeValue = "";
		if (OperateMode.VALUE2LABEL == verifyDict.operateMode()) {
			for (BasicSysDict item : dictList) {
				if (type.equals(item.getType()) && value.toString().equals(item.getValue())) {
					storeValue = item.getLabel();
					break;
				}
			}
		} else {
			for (BasicSysDict item : dictList) {
				if (type.equals(item.getType()) && value.toString().equals(item.getLabel())) {
					storeValue = item.getValue();
					break;
				}
			}
		}
		return storeValue;
	}

	/**
	 * 给对应的字段设置属性值。 注：由于对象可能存在继承关系，因此查询属性时需要查询对应的父类。
	 * 
	 * @param data           对象数据
	 * @param storeFieldName 属性名称
	 * @param storeValue     属性值
	 * @param error          错误信息列表
	 *
	 * @author zhouhui
	 * @since 2022-05-08
	 */
	private static void setStoreFieldValue(Object data, String storeFieldName, String storeValue,
			List<DictionaryError> error) {
		Class<? extends Object> tempCls = data.getClass();
		Field storeField = null;
		while (tempCls != null) {
			try {
				storeField = tempCls.getDeclaredField(storeFieldName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (storeField != null) {
				break;
			}
			tempCls = tempCls.getSuperclass();
		}

		if (storeField == null) {
			DictionaryError errorItem = new DictionaryError();
			errorItem.setFieldName(storeFieldName);
			errorItem.setMessage("没有查询到" + storeFieldName + "字段");
			error.add(errorItem);
		} else {
			ReflectionUtils.makeAccessible(storeField);
			ReflectionUtils.setField(storeField, data, storeValue);
		}
	}
}
