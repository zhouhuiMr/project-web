package com.web.service.aliyun;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.web.common.properties.ServerConfigEntiy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "服务器配置信息对象", description = "服务器配置信息对象")
@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliyunConfigEntity {

	/** 服务器名称 */
    @Schema(title = "服务器配置列表")
	private List<ServerConfigEntiy> ecs;
    
    /** AccessKeyId */
    @Schema(title = "AccessKeyId")
    @Value("${aliyun.access.key.id}")
	private String accessKeyId;
	
    /** AccessKeySecret */
    @Schema(title = "AccessKeySecret")
    @Value("${aliyun.access.key.secret}")
	private String accessKeySecret;
    
    /** 连接超时时间 */
    @Schema(title = "连接超时时间")
    @Value("${aliyun.http.connection-timeout}")
    private Integer connectionTimeout;
    
    /** 最大连接数 */
    @Schema(title = "最大连接数")
    @Value("${aliyun.http.max-idle-connections}")
    private Integer maxIdleConnections;
    
    /** 读取超时时间 */
    @Schema(title = "读取超时时间")
    @Value("${aliyun.http.read-timeout}")
    private Integer readTimeout;
}
