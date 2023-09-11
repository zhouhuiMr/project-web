package com.web.common.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.web.common.dictionary.VerifyDictField;
import com.web.common.dictionary.VerifyDictField.OperateMode;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 对应的菜单
 * </p>
 *
 * @author zhouhui
 * @since 2022-04-21
 */
@Getter
@Setter
@TableName("web_menu")
@Schema(title = "WebMenu对象", description = "对应的菜单")
public class WebMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 唯一标识 主键 */
    @Schema(title = "唯一标识 主键")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Integer menuId;

    /** 菜单名称 */
    @Length(max = 32,message = "菜单名称不能超过32个字符")
    @NotBlank(message = "未填写菜单名称")
    @Schema(title = "菜单名称")
    private String menuName;

    /** 菜单的顺序 菜单的顺序编号，排序从小到大进行排序 */
    @NotNull(message = "未设置菜单顺序")
    @Schema(title = "菜单的顺序 菜单的顺序编号，排序从小到大进行排序")
    private Integer menuOrder;

    /** 菜单对应的图标地址，或者图标信息 */
    @Schema(title = "菜单对应的图标地址，或者图标信息")
    private String icon;

    /** 菜单访问地址 */
    @Schema(title = "菜单访问地址")
    private String menuUrl;

    /** 父菜单的唯一标识 */
    @NotNull(message = "未选择父菜单")
    @Schema(title = "父菜单的唯一标识")
    private Integer parentId;

    /** 菜单的适用范围 默认0，0客户端使用；1管理端使用 */
    @NotNull(message = "未设置菜单的适用范围")
    @VerifyDictField(operateMode = OperateMode.VERIFY,dictType = "menu_scope")
    @Schema(title = "菜单的适用范围 默认0，0客户端使用；1管理端使用")
    private Integer menuScope;

    /** 菜单的类型 默认0，0、当前页面进行展示；1、新的页签进行展示 */
    @NotNull(message = "未设置菜单的类型")
    @VerifyDictField(operateMode = OperateMode.VERIFY,dictType = "menu_type")
    @Schema(title = "菜单的类型 默认0，0、当前页面进行展示；1、新的页签进行展示")
    private Integer menuType;

    /** 菜单等级 默认1级，一级菜单，编号1、2、3...... */
    @NotNull(message = "未设置菜单的级别")
    @VerifyDictField(operateMode = OperateMode.VERIFY,dictType = "menu_level")
    @Schema(title = "菜单等级 默认1级，一级菜单，编号1、2、3......")
    private Integer menuLevel;

    /** 是否在菜单展示;默认0，展示；1不展示'  */
    @Schema(title = "是否在菜单展示;默认0，展示；1不展示' ")
    private Boolean isShow;

    /** 是否删除；默认0，0否；1是 */
    @Schema(title = "是否删除；默认0，0否；1是")
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
