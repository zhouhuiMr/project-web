package com.web.service.handler.error;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class SystemErrorHandler implements Ordered{
	
	private static final String API_URL_KEY = "apiUrl:";
	
	private static final String REASON_KEY = "reason:";

	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	public R<Object> paramErrorException(HttpServletRequest req, HttpMessageNotReadableException e){
		R<Object> json = new R<>();
		json.setResultEnum(ResultEnum.PARAMETER_ERROR);
		StringBuilder bd = new StringBuilder();
		bd.append("\r\n");
		bd.append(API_URL_KEY + req.getServletPath() + "\r\n");
		bd.append(REASON_KEY + e.getMessage());
		log.error(bd.toString());
		return json;
	}
	
	@ExceptionHandler(value = MaxUploadSizeExceededException.class)
	public R<Object> uploadException(HttpServletRequest req, MaxUploadSizeExceededException e){
		R<Object> json = new R<>();
		json.setResultEnum(ResultEnum.FILE_UPLOAD_SIZE_ERROR);
		StringBuilder bd = new StringBuilder();
		bd.append("\r\n");
		bd.append(API_URL_KEY + req.getServletPath() + "\r\n");
		bd.append(REASON_KEY + e.getMessage());
		log.error(bd.toString());
		return json;
	}
	
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	public R<Object> uploadException(HttpServletRequest req, MissingServletRequestParameterException e){
		R<Object> json = new R<>();
		json.setResultEnum(ResultEnum.PARAMETER_ERROR);
		StringBuilder bd = new StringBuilder();
		bd.append("\r\n");
		bd.append(API_URL_KEY + req.getServletPath() + "\r\n");
		bd.append(REASON_KEY + e.getMessage());
		log.error(bd.toString());
		return json;
	}
	
	/**
	 * 此异常放在最后，如果没有单独的异常处理时再执行此异常
	 * @param req 请求信息
	 * @param e 异常信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	@ExceptionHandler(value = Exception.class)
	public R<Object> globalException(HttpServletRequest req, Exception e){
		R<Object> json = new R<>();
		json.setResultEnum(ResultEnum.ERROR);
		StringBuilder bd = new StringBuilder();
		bd.append("\r\n");
		bd.append(API_URL_KEY + req.getServletPath() + "\r\n");
		bd.append(REASON_KEY + e.getMessage());
		log.error(bd.toString());
		return json;
	}
	
	@Override
	public int getOrder() {
		return -2;
	}
	
}
