package com.web.common.user.api;

import java.util.List;

import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.common.api.config.ServiceInstanceName;
import com.web.common.result.R;
import com.web.common.user.entity.UserMenu;
import com.web.common.user.entity.WebMenuEntity;

@FeignClient(name = ServiceInstanceName.SERVICE_USER_NAME, contextId = "menuApi",path = "/user/menu")
public interface MenuApi {

	/**
	 * 获取菜单列表
	 * @param webMenuEntity 菜单的查询条件
	 * @return R<IPage<WebMenuEntity>> 菜单列表
	 *
	 * @author zhouhui
	 * @since 2022-04-17
	 */
	@PostMapping(path = "/list")
	@ResponseBody
	R<IPage<WebMenuEntity>> getMenuList(@RequestBody WebMenuEntity webMenuEntity); 
	
	
	/**
	 * 新增菜单
	 * @param webMenuEntity 菜单信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-04-17
	 */
	@PostMapping(path = "/save")
	@ResponseBody
	R<Object> saveMenu(@RequestBody @Valid WebMenuEntity webMenu);
	
	/**
	 * 菜单更新
	 * @param webMenuEntity 菜单信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-08
	 */
	@PostMapping(path = "/update")
	@ResponseBody
	R<Object> updateMenu(@RequestBody @Valid WebMenuEntity webMenu);
	
	/**
	 * 标记删除菜单信息
	 * @param webMenu 菜单信息
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-08
	 */
	@PostMapping(path = "/delete")
	@ResponseBody
	R<Object> delMenu(@RequestBody WebMenuEntity webMenu);
	
	/**
	 * 获取当前登录用户的菜单
	 * @return R<Object> 处理结果
	 *
	 * @author zhouhui
	 * @since 2022-05-12
	 */
	@PostMapping(path = "")
	@ResponseBody
	R<List<UserMenu>> getUserMenu();
	
}
