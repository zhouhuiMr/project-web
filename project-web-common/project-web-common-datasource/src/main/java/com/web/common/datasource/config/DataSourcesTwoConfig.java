package com.web.common.datasource.config;

import javax.sql.DataSource;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.RequiredArgsConstructor;

@Profile(value = "dataSourceTwo")
@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class DataSourcesTwoConfig {
	
	private final MybatisPlusInterceptor paginationInterceptor;
	
	private final Environment environment;

	@Bean(name="dataSourceTwo")
	@ConfigurationProperties(value="spring.datasource.druid.two")
	public DataSource dataSourceTwo(){
		return DruidDataSourceBuilder.create().build();
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactoryTwo() throws Exception{
		MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
		bean.setGlobalConfig(GlobalConfigUtils.defaults().setBanner(false));
		bean.setDataSource(dataSourceTwo());
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));
		
		bean.setConfiguration(mybatisConfiguration());
		
		bean.setPlugins(paginationInterceptor);
		return bean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTempTwo() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactoryTwo());
	}
	
	@Bean
	public PlatformTransactionManager txManagerTwo() {
		return new DataSourceTransactionManager(dataSourceTwo());
	}
	
	/**
	 * 设置mybatis的配置信息。
	 * 配置信息查询地址 https://mybatis.org/mybatis-3/zh/configuration.html
	 * @return Properties mybatis的配置信息
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private MybatisConfiguration mybatisConfiguration() {
		MybatisConfiguration config = new MybatisConfiguration();
		config.setLogImpl(StdOutImpl.class);
		if(containProfile("prd")) {
			config.setLogImpl(NoLoggingImpl.class);
		}
		return config;
	}
	
	/**
	 * 是否包含查询的profile
	 * @param profile profile值
	 * @return boolean true包含；false不包含
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private boolean containProfile(String profile) {
		if(!StringUtils.hasText(profile)) {
			return false;
		}
		String[] profiles = environment.getActiveProfiles();
		if(profiles.length == 0) {
			return false;
		}
		for(int i=0;i<profiles.length;i++) {
			if(profile.equals(profiles[i])) {
				return true;
			}
		}
		return false;
	}
}
