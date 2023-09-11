package com.web.common.basic.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 定时任务名称
 * </p>
 *
 * @author zhouhui
 * @since 2023-08-08
 */
@Getter
@Setter
@TableName("quartz_job")
@Schema(title = "QuartzJob对象", description = "定时任务名称")
public class QuartzJob implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    @Schema(title = "自增主键")
    @TableId(value = "job_id", type = IdType.AUTO)
    private Integer jobId;

    /** 定时任务名称 */
    @Schema(title = "定时任务名称")
    private String jobName;

    /** 任务备注说明 */
    @Schema(title = "任务备注说明")
    private String jobRemark;

    /** 是否删除，0否；1是 */
    @Schema(title = "是否删除，0否；1是")
    private Integer delFlag;

    /** 创建人的唯一标识 */
    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    /** 创建时间 */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /** 修改人的唯一标识 */
    @Schema(title = "修改人的唯一标识")
    private Integer changeUserId;

    /** 修改时间 */
    @Schema(title = "修改时间")
    private LocalDateTime changeTime;


}
