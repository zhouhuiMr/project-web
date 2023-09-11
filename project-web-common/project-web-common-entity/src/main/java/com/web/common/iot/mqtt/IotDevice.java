package com.web.common.iot.mqtt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 设备信息
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-07
 */
@Getter
@Setter
@TableName("iot_device")
@Schema(title = "IotDevice对象", description = "设备信息")
public class IotDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "device_id", type = IdType.AUTO)
    private Integer deviceId;

    /** 设备名称（显示） */
    @NotBlank(message = "未设置设备名称")
    @Schema(title = "设备名称（显示）")
    private String deviceName;

    /** 设备连接用户名 */
    @Schema(title = "设备连接用户名")
    private String deviceKey;

    /** 设备连接的密码 */
    @Schema(title = "设备连接的密码")
    private String deviceSecret;

    /** 设备在线状态，0初始化；1离线；2在线 */
    @Schema(title = "设备在线状态，0初始化；1离线；2在线")
    private Integer deviceStatus;

    /** 是否有效，默认0，0有效，1无效 */
    @Schema(title = "是否有效，默认0，0有效，1无效")
    private Integer delFlag;

    /** 创建人的唯一标识 */
    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人的唯一标识 */
    @Schema(title = "修改人的唯一标识")
    private Integer updateUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime updateTime;


}
