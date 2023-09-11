package com.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.common.user.entity.WebUserDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户对应的部门信息 Mapper 接口
 * </p>
 *
 * @author zhouhui
 * @since 2023-05-25
 */
@Mapper
public interface WebUserDeptMapper extends BaseMapper<WebUserDept> {

}
