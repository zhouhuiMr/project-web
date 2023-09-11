package com.web.common.basic.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "服务器内存信息对象", description = "服务器内存信息对象")
public class ServerCpuEntity extends ServerBasicEntity{
	
}
