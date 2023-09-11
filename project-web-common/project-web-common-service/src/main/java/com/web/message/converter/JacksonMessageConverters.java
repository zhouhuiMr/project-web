package com.web.message.converter;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.feign.interceptor.ResponseBodyFeignDecoder;
import feign.codec.Decoder;

/**
 * 全局配置时间类型的转换
 * @author zhouhui
 * @since 1.0.0
 */
@Configuration
public class JacksonMessageConverters implements WebMvcConfigurer{

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = getMappingJackson2HttpMessageConverter();
		converters.add(0, new ByteArrayHttpMessageConverter());
		converters.add(1, converter);
	}
	
	@Bean
	public Decoder feignDecoder() {
		return new ResponseBodyFeignDecoder();
	}
	
	/**
	 * 创建json转换
	 * @return MappingJackson2HttpMessageConverter
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(CustomJsonAnalysis.setSimpleModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		converter.setObjectMapper(mapper);
		return converter;
	}
}
