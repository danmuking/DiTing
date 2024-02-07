package com.luo.j2cache.sb.autoconfigure;

import com.luo.j2cache.sb.cache.support.util.SpringJ2CacheConfigUtil;
import com.luo.j2cache.sb.cache.support.util.SpringUtil;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 启动入口
 *
 * @author luohq
 * @date 2022-04-07 09:28
 */
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({J2CacheConfigProps.class})
@Configuration
public class J2CacheAutoConfiguration {

    private RedisProperties redisProperties;

    public J2CacheAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * 转换SpringBoot配置为J2Cache底层配置
     */
    @Bean
    public J2CacheConfig j2CacheConfig(J2CacheConfigProps j2CacheConfigProps) {
        //覆盖设置redis database（以Spring Data Redis为准）
        j2CacheConfigProps.getL2().getRedis().setDatabase(this.redisProperties.getDatabase());
        //转换SpringBoot配置为J2Cache底层配置
        J2CacheConfig cacheConfig = SpringJ2CacheConfigUtil.initFromConfig(j2CacheConfigProps);
        return cacheConfig;
    }

    /**
     * J2Cache核心类CacheChannel
     */
    @Bean
    @DependsOn({"springUtil", "j2CacheConfig"})
    public CacheChannel cacheChannel(J2CacheConfig j2CacheConfig) {
        J2CacheBuilder builder = J2CacheBuilder.init(j2CacheConfig);
        return builder.getChannel();
    }

    /**
     * Spring工具类
     */
    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }

}
