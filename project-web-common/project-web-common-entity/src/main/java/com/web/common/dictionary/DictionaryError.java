package com.web.common.dictionary;

import com.web.common.dictionary.VerifyDictField.OperateMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryError {

	/** 字段名称 */
	private String fieldName;
	
	/** 字典类型 */
	private String type;
	
	/** 操作类型 */
	private OperateMode operateMode;
	
	/** 错误信息 */
	private String message;
}
