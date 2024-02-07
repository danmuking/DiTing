package com.luo.j2cache.sb.autoconfigure;

import com.luo.j2cache.sb.cache.support.redis.SpringRedisProvider;
import com.luo.j2cache.sb.cache.support.redis.SpringRedisPubSubPolicy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * J2Cache相关的配置信息
 *
 * @author luohq
 * @date 2022-04-06 16:30
 */
@Data
@ConfigurationProperties(prefix = "j2cache")
public class J2CacheConfigProps {
    /**
     * 是否开启spring cache缓存,注意:开启后需要添加spring.cache.type=GENERIC,将缓存类型设置为GENERIC
     */
    private Boolean openSpringCache = false;

    /**
     * 缓存清除模式，
     * <ul>
     * <li>active:主动清除，二级缓存过期主动通知各节点清除，优点在于所有节点可以同时收到缓存清除</li>
     * <li>passive:被动清除，一级缓存过期进行通知各节点清除一二级缓存，</li>
     * <li>blend:两种模式一起运作，对于各个节点缓存准确以及及时性要求高的可以使用，正常用前两种模式中一个就可</li>
     * </ul>
     */
    private String cacheCleanMode = "passive";

    /**
     * 是否缓存null对象（默认false）<br/>
     * 注：同时设置J2Cache和Spring Cache
     */
    private Boolean defaultCacheNullObject = false;
    /**
     * 缓存变更广播方式（jgroups | redis | lettuce | rabbitmq | rocketmq | none | 自定义class）<br/>
     * 注：默认SpringBoot Redis自定义实现SpringRedisPubSubPolicy
     */
    private String broadcast = SpringRedisPubSubPolicy.class.getName();

    /**
     * L1级缓存配置
     */
    private L1Config l1 = new L1Config();
    /**
     * L2缓存配置
     */
    private L2Config l2 = new L2Config();
    /**
     * 兼容原J2Cache属性配置
     */
    private Properties properties = new Properties();


    /**
     * L1缓存配置
     */
    @Data
    public static class L1Config {
        /**
         * L1 缓存provider名称（caffeine | ehcache | ehcache3）
         */
        private String providerClass = "caffeine";
        /**
         * Caffeine缓存配置
         */
        private CaffeineConfig caffeine = new CaffeineConfig();
    }

    /**
     * Caffeine配置
     */
    @Data
    public static class CaffeineConfig {
        /**
         * Caffeine缓存region配置<br/>
         * 格式如下（目前仅支持配置设置cache中最多缓存对象数量及过期时间）：
         * [regionName] = size, xxxx[s|m|h|d]
         */
        private Map<String, String> region = new HashMap<>();
    }

    /**
     * L2缓存配置
     */
    @Data
    public static class L2Config {
        /**
         * L2 缓存provider名称（redis | lettuce | readonly-redis | memcached）<br/>
         * 注：默认SpringBoot Redis自定义实现SpringRedisProvider
         */
        private String providerClass = SpringRedisProvider.class.getName();
        /**
         * 二级缓存序列化格式（fst（推荐） | kryo | json | fastjson | java（默认） | fse | 自定义classname）
         */
        private String serialization = "fastjson";
        /**
         * 二级缓存配置属性前缀（若使用Spring Data Redis则此处可为空）
         */
        private String configSection;
        /**
         * Redis配置
         */
        private RedisConfig redis = new RedisConfig();
    }

    /**
     * Redis配置
     */
    @Data
    public static class RedisConfig {
        /**
         * 是否启用同步一级缓存的Time-To-Live超时时间到Redis TTL（true启用，false不启用则永不超时）
         */
        private Boolean syncTtlToRedis = false;
        /**
         * Key命名空间（前缀）
         */
        private String namespace = "";
        /**
         * Redis存储模式（generic | hash）
         */
        private String storage = "generic";
        /**
         * Redis发布订阅（Pub/Sub）对应的channel名称
         */
        private String channel = "j2cache";
        /**
         * Redis DB序号
         */
        private Integer database = 0;

    }


}
