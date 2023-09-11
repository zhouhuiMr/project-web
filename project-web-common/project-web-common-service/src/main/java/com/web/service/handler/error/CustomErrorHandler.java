package com.web.service.handler.error;

import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;

/**
 * 自定义异常错误处理信息
 * @author zhouhui
 * @since 1.0.0
 */
@RestControllerAdvice
public class CustomErrorHandler implements Ordered{

	@ExceptionHandler(value = CustomException.class)
	public R<Object> customErrorException(CustomException e){
		R<Object> json = new R<>();
		json.setResultEnum(ResultEnum.ERROR);
		json.setMessage(e.getMessage());
		return json;
	}

	@Override
	public int getOrder() {
		return -3;
	}
}
