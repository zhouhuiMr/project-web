package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author zhouhui
 * @since 2022-02-06
 */
@Getter
@Setter
@TableName("web_user")
@Schema(title = "WebUser对象", description = "用户信息")
public class WebUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "主键，唯一标识")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @Length(max = 20, message = "工号最多20个字符")
    @NotBlank(message = "未设置工号")
    @Schema(title = "编号")
    private String code;

    @JsonIgnore
    @Schema(title = "登录的密码")
    private String password;

    @Schema(title = "手机号")
    private String tel;

    @Schema(title = "邮箱地址")
    private String mail;

    @Length(max = 20, message = "昵称最多20个字符")
    @NotBlank(message = "未设置昵称")
    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "所在省份")
    private String province;

    @Schema(title = "所在市")
    private String city;

    @Schema(title = "所在区")
    private String district;

    @Schema(title = "出生年月")
    private String birthday;

    @Schema(title = "头像地址")
    private String icon;

    @Schema(title = "登录用户的类别，默认0，0客户端普通用户；1管理端用户")
    private String userType;

    @Schema(title = "用户的状态，默认0，0注册（默认状态）；1有效（可以登录系统）；2无效。")
    private String status;
    
    @Schema(title = "创建人的唯一标识")
    private Integer createUserId;

    @Schema(title = "数据创建时间")
    private LocalDateTime createTime;

    @Schema(title = "修改人的唯一标识")
    private Integer updateUserId;
    
    @Schema(title = "数据修改时间")
    private LocalDateTime updateTime;
    
    @Schema(title = "角色的唯一标识")
    @TableField(exist = false)
    private List<Integer> roles;
}
