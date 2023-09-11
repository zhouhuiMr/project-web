package com.web.common.iot.mqtt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * mqtt消息主题
 * </p>
 *
 * @author zhouhui
 * @since 2023-06-08
 */
@Getter
@Setter
@TableName("iot_mqtt_topic")
@Schema(title = "IotMqttTopic对象", description = "mqtt消息主题")
public class IotMqttTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "topic_id", type = IdType.AUTO)
    @Schema(title = "主键")
    private Integer topicId;

    /** 传输机制，0至多一次、1至少一次、2仅一次 */
    @NotNull(message = "未填写传输机制")
    @Schema(title = "传输机制，0至多一次、1至少一次、2仅一次")
    private Integer topicQos;

    /** 消息主题内容 */
    @NotBlank(message = "未填写消息主题")
    @Schema(title = "消息主题内容")
    private String topicName;

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
