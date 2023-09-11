package com.web.service.handler.error;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.Symbols;

/**
 * 针对valid错误信息配置处理信息
 * 
 * @author zhouhui
 * @since 1.0.0
 */
@RestControllerAdvice
public class ValidErrorHandler implements Ordered{

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public R<Object> validErrorException(MethodArgumentNotValidException e) {
		R<Object> json = new R<>();
		BindingResult result = e.getBindingResult();
		FieldError fieldError = result.getFieldError();
		if (fieldError == null) {
			json.setResultEnum(ResultEnum.ERROR);
			return json;
		}
		if (result.getFieldErrorCount() > 1) {
			List<FieldError> fieldErrorList = result.getFieldErrors();
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < fieldErrorList.size(); i++) {
				FieldError item = fieldErrorList.get(i);
				if (i > 0) {
					builder.append(Symbols.CN_SEMICOLON);
				}
				builder.append(item.getDefaultMessage());
				json.setResultEnum(ResultEnum.PARAMETER_ERROR);
				json.setMessage(builder.toString());
			}
			return json;
		}
		json.setResultEnum(ResultEnum.PARAMETER_ERROR);
		json.setMessage(fieldError.getDefaultMessage());
		return json;
	}

	@Override
	public int getOrder() {
		return -4;
	}
}
