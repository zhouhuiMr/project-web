package com.web.common.basic.entity;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务器看板数据
 * @author zhouhui
 */
@Getter
@Setter
@Schema(title = "服务器数据看板信息对象", description = "服务器数据看板信息对象")
public class ServerBoardEntity {
	
	/** 服务器实例名称 */
    @Schema(title = "服务器实例名称")
	private String instanceId;
    
    /** 服务器所在地区 */
    @Schema(title = "服务器所在地区")
    private String regionId;
    
    /** 服务器内存大小（单位：M） */
    @Schema(title = "服务器内存大小（单位：M）")
    private Integer memorySize;
    
    /** 服务器CPU核心数 */
    @Schema(title = "服务器CPU核心数")
    private Integer cpu;
    
    /** 公网入最大带宽 */
    @Schema(title = "公网入最大带宽")
    private Integer bandwithIn;
    
    /** 公网出最大带宽 */
    @Schema(title = "公网出最大带宽")
    private Integer bandwithOut;
    
    /** 实例状态。取值范围：Pending：创建中；Running：运行中；Starting：启动中；Stopping：停止中；Stopped：已停止 */
    @Schema(title = "实例状态。取值范围：Pending：创建中；Running：运行中；Starting：启动中；Stopping：停止中；Stopped：已停止")
    private String status;
    
    /** CPU信息 */
    @Schema(title = "CPU信息")
    private ServerCpuEntity serverCpuEntity;
    
    /** 内存信息 */
    @Schema(title = "内存信息")
    private ServerMemoryEntity serverMemoryEntity;
    
    /** 云盘/本地盘列表 */
    @Schema(title = "云盘/本地盘列表")
    private List<ServerDiskEntity> diskList;
}
