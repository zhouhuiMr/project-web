package com.web.common.iot.mqtt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 设备对应的消息主题
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-22
 */
@Getter
@Setter
@TableName("iot_device_topic")
@Schema(title = "IotDeviceTopic对象", description = "设备对应的消息主题")
public class IotDeviceTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(title = "主键")
    @TableId(value = "main_id", type = IdType.AUTO)
    private Integer mainId;

    /** 设备的主键 */
    @NotNull(message = "请选择设备id")
    @Schema(title = "设备的主键")
    private Integer deviceId;

    /** 消息主题的id */
    @NotNull(message = "请选择消息主题id")
    @Schema(title = "消息主题的id")
    private Integer topicId;

    /** 关联状态，0关联；1不关联 */
    @Schema(title = "关联状态，0关联；1不关联")
    private Integer status;

    /** 创建人 */
    @Schema(title = "创建人")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人 */
    @Schema(title = "修改人")
    private Integer updateUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime updateTime;


}
