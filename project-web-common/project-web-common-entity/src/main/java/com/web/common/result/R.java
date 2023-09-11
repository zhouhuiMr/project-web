package com.web.common.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "处理结果")
public class R<T>{
	
	@Schema(title = "处理结果编号")
	private String code = "";
	
	@Schema(title = "数据信息")
	private T data;
	
	@Schema(title = "处理结果信息")
	private String message = "";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setResultEnum(ResultEnum result) {
		this.code = result.getCode();
		this.message = result.getMessage();
	}
}
