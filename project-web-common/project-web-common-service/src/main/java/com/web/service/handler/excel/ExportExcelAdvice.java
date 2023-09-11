package com.web.service.handler.excel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.alibaba.excel.EasyExcelFactory;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.common.tool.DateTimeUtil;

/**
 * 导出excel处理方法，使用Easy Excel来进行处理。<br>
 * 1、通过{@code ExportExcel}标记Controller需要返回Excel；<br>
 * 2、设置controller方法返回需要导出Excel的List数据。<br>
 *
 * @author zhouhui
 * @since 1.0.0
 */
@RestControllerAdvice
public class ExportExcelAdvice implements ResponseBodyAdvice<Object>,Ordered{

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		ExportExcel exportExcel = returnType.getMethodAnnotation(ExportExcel.class);
		return exportExcel != null;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		ExportExcel exportExcel = returnType.getMethodAnnotation(ExportExcel.class);
		if(exportExcel == null) {
			return body;
		}
		ServletServerHttpResponse res = null;
		if(response instanceof ServletServerHttpResponse) {
			res = (ServletServerHttpResponse) response;
		}
		if(res == null || !(body instanceof List)) {
			return body;
		}
		
		Class<?> cls = getDataType(returnType);
		if(cls == null) {
			return body;
		}
		
		HttpServletResponse httpRes = res.getServletResponse();
		httpRes.setContentType("application/vnd.ms-excel");
		httpRes.setCharacterEncoding("utf-8");
		//获取文件名
		String fileName = "";
		try {
			String temp = exportExcel.fileName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeUtil.YEAR_MONTH_DATE_TIME_NO_CONNECTOR));
			fileName = URLEncoder.encode(temp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String sheetName = exportExcel.sheetName();
		if(!StringUtils.hasText(sheetName)) {
			sheetName = exportExcel.fileName();
		}
		List<?> list = (List<?>) body;
		httpRes.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		httpRes.setHeader("filename", fileName);
		try {
			EasyExcelFactory.write(httpRes.getOutputStream(), cls).autoCloseStream(Boolean.FALSE).sheet(sheetName)
			.doWrite(list);
		} catch (IOException e) {
			e.printStackTrace();
			return setErrorResponseBody(httpRes);
		}
		return null;
	}
	
	/**
	 * 获取List中包含元素的具体类型
	 * @param returnType http返回信息
	 * @return Class<?> 具体类型
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private Class<?> getDataType(MethodParameter returnType){
		Type type = returnType.getGenericParameterType();
		ParameterizedType paramType = null;
		if(type instanceof ParameterizedType) {
			paramType = (ParameterizedType) type;
		}
		if(paramType == null) {
			return null;
		}
		Type[] types = paramType.getActualTypeArguments();
		if(types != null && types.length > 0) {
			try {
				return Class.forName(types[0].getTypeName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 返回错误信息
	 * @param res 
	 * @return 错误信息对象
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private Object setErrorResponseBody(HttpServletResponse res) {
		R<Object> json = new R<>();
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		json.setResultEnum(ResultEnum.EXPORT_EXCEL_ERROR);
		return json;
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
