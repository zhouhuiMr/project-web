package com.web.tool;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.result.R;
import io.netty.buffer.PooledByteBufAllocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
public class ServerHttpUtil {
	public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
	
	private ServerHttpUtil() {
	}
	
	/**
	 * 将请求的返回结果转成DataBuffer
	 * @param json 需要处理的返回结果
	 * @return DataBuffer
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static DataBuffer getDataBuffer(R<Object> json) {
		String jsonStr = "";
		if(json == null) {
			jsonStr = "";
		}else {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonStr = mapper.writeValueAsString(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return stringToDataBuffer(jsonStr);
	}
	
	/**
	 * 将字符串转成DataBuffer对象
	 * @param str 字符串
	 * @return DataBuffer
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static DataBuffer stringToDataBuffer(String str) {
		if(str == null) {
			str = "";
		}
		NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(new PooledByteBufAllocator());
		return bufferFactory.wrap(str.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * 设置response的返回信息
	 * @param res ServerHttpResponse对象
	 * @param json 处理结果
	 * @return Mono<Void>
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static Mono<Void> setResponse(ServerHttpResponse res,R<Object> json) {
		DataBuffer body = ServerHttpUtil.getDataBuffer(json);
		res.setRawStatusCode(HttpStatus.OK.value());
		res.getHeaders().set("Content-Type", APPLICATION_JSON_UTF8);
		return res.writeAndFlushWith(Flux.just(Flux.just(body)));
	}
}
