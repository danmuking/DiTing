package com.luo.j2cache.sb.autoconfigure;

import com.luo.j2cache.sb.cache.support.J2CacheCacheManger;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 开启对spring cache支持的配置入口
 *
 * @author luohq
 * @date 2022-04-06 17:33
 */
@Configuration
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({ J2CacheConfigProps.class, CacheProperties.class })
@ConditionalOnProperty(name = "j2cache.open-spring-cache", havingValue = "true")
@EnableCaching
public class J2CacheSpringCacheAutoConfiguration {
	/**
	 * Spring Cache属性
	 */
	private final CacheProperties cacheProperties;
	/**
	 * J2Cache SpringBoot属性
	 */
	private final J2CacheConfigProps j2CacheConfigProps;

	J2CacheSpringCacheAutoConfiguration(CacheProperties cacheProperties, J2CacheConfigProps j2CacheConfigProps) {
		this.cacheProperties = cacheProperties;
		this.j2CacheConfigProps = j2CacheConfigProps;
	}

	/**
	 * 自定义J2Cache CacheManager实现
	 */
	@Bean
	@ConditionalOnBean(CacheChannel.class)
	public J2CacheCacheManger cacheManager(CacheChannel cacheChannel) {
		List<String> cacheNames = cacheProperties.getCacheNames();
		J2CacheCacheManger cacheCacheManger = new J2CacheCacheManger(cacheChannel);
		//是否缓存null值
		cacheCacheManger.setAllowNullValues(j2CacheConfigProps.getDefaultCacheNullObject());
		//初始预加载cache
		cacheCacheManger.setCacheNames(cacheNames);
		return cacheCacheManger;
	}


}
