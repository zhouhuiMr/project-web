package com.web.feign.interceptor;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.result.ResultEnum;
import com.web.message.converter.CustomJsonAnalysis;
import com.web.service.handler.error.CustomException;
import feign.FeignException;
import feign.Response;
import feign.Response.Body;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;

/**
 * @author zhouhui
 * @since 1.0.0
 */
public class ResponseBodyFeignDecoder implements Decoder{
	
	public static final String CONTENT_TYPE_NAME = "content-type";
	public static final String MEDIA_IMAGE_STRING = "media_image";
	private List<String> mediaImageTypeList = Arrays.asList(MediaType.IMAGE_GIF_VALUE,
			MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public ResponseBodyFeignDecoder() {
		mapper.registerModule(CustomJsonAnalysis.setSimpleModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
		if(HttpStatus.OK.value() != response.status()) {
			throw new CustomException(ResultEnum.FEIGN_ERROR.getMessage());
		}
		if(response.body() == null) {
			throw new CustomException(ResultEnum.FEIGN_NO_DATA_ERROR.getMessage());
		}
		return responseBodyConverter(response, type);
	}

	/**
	 * 将返回结果转成对象
	 * @param response 返回结果
	 * @param type 需要转成对象的类型
	 * @return Object 转换完成的对象的值
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private Object responseBodyConverter(Response response, Type type) {
		Body body = response.body();
		Object converterValue = null;
		String contentType = getResponseContentType(response);

		if(MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
			try {
				Reader reader = body.asReader(StandardCharsets.UTF_8);
				String bodyStr = Util.toString(reader);
				if(StringUtils.hasText(bodyStr)) {
					JavaType javaType = mapper.constructType(type);
					converterValue = mapper.readValue(bodyStr, javaType);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(converterValue == null) {
				throw new CustomException(ResultEnum.FEIGN_ERROR.getMessage());
			}
		}else if(MEDIA_IMAGE_STRING.equals(contentType) && type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			if(paramType.getRawType().getTypeName().equals(ResponseEntity.class.getName())) {
				return new ResponseEntity<>(body, HttpStatus.OK);
			}
		}
		return converterValue;
	}
	
	/**
	 * 获取Response的headers，并判断返回值类型
	 * @param response 返回结果
	 * @return String 返回值类型
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private String getResponseContentType(Response response) {
		Map<String, Collection<String>> headers = response.headers();
		if(headers == null || headers.isEmpty()) {
			return "";
		}
		Collection<String> contentTypeList = headers.get(CONTENT_TYPE_NAME);
		if(contentTypeList == null || contentTypeList.isEmpty()) {
			return "";
		}
		for(String item : contentTypeList) {
			if(MediaType.APPLICATION_JSON_VALUE.indexOf(item) >= 0) {
				return MediaType.APPLICATION_JSON_VALUE;
			}else if(MediaType.APPLICATION_FORM_URLENCODED_VALUE.indexOf(item) >= 0) {
				return MediaType.APPLICATION_FORM_URLENCODED_VALUE;
			}else if(mediaImageTypeList.contains(item)) {
				return MEDIA_IMAGE_STRING;
			}
		}
		return "";
	}
}
