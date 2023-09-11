package com.web.common.datasource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * Mybatis-plus配置信息
 * 
 * @author zhouhui
 */
@Configuration
public class MybatisPlusConfig {

	/**
	 * Mybatis-plus 分页插件
	 * @return MybatisPlusInterceptor
	 *
	 * @author zhouhui
	 * @since 2022.01.28
	 */
	@Bean
	public MybatisPlusInterceptor paginationInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return interceptor;
	}
}
