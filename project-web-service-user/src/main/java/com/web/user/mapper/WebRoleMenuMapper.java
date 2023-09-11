package com.web.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.user.entity.WebRoleMenu;

/**
 * <p>
 * 角色对应的菜单和 取消关联关系时可以直接删除数据 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2022-05-14
 */
@Mapper
public interface WebRoleMenuMapper extends BaseMapper<WebRoleMenu> {

}
