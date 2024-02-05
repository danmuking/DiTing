package com.luo.j2cache.sb.cache.support.util;

import com.luo.j2cache.sb.autoconfigure.J2CacheConfigProps;
import net.oschina.j2cache.J2CacheConfig;

import java.util.Objects;
import java.util.Properties;

/**
 * Spring J2Cache配置工具
 *
 * @author luohq
 * @date 2022-04-07
 */
public class SpringJ2CacheConfigUtil {

	public static final String DOT_STR = ".";
	public static final String PREFIX_REGION = "region.";

	/**
	 * 从spring环境变量中查找j2cache配置
	 * @param j2CacheConfigProps spring相关配置属性
	 * @return j2cache configuration
	 */
	public final static J2CacheConfig initFromConfig(J2CacheConfigProps j2CacheConfigProps){
		J2CacheConfig config = new J2CacheConfig();
		config.setSerialization(j2CacheConfigProps.getL2().getSerialization());
		config.setBroadcast(j2CacheConfigProps.getBroadcast());
		config.setL1CacheName(j2CacheConfigProps.getL1().getProviderClass());
		config.setL2CacheName(j2CacheConfigProps.getL2().getProviderClass());
		config.setSyncTtlToRedis(j2CacheConfigProps.getL2().getRedis().getSyncTtlToRedis());
		config.setDefaultCacheNullObject(j2CacheConfigProps.getDefaultCacheNullObject());
		String l2_config_section = j2CacheConfigProps.getL2().getConfigSection();
		if (l2_config_section == null || Objects.equals(l2_config_section.trim(),"")) {
			l2_config_section = config.getL2CacheName();
		}
		final String l2_section = l2_config_section;
		//转换L1缓存属性
		matchPropsPrefixAndSet(j2CacheConfigProps.getProperties(), config.getL1CacheProperties(), config.getL1CacheName());
		//转换L2缓存属性
		matchPropsPrefixAndSet(j2CacheConfigProps.getProperties(), config.getL2CacheProperties(), l2_section);
		//转换广播属性
		matchPropsPrefixAndSet(j2CacheConfigProps.getProperties(), config.getBroadcastProperties(), config.getBroadcast());

		//特殊处理caffeine配置
		j2CacheConfigProps.getL1().getCaffeine().getRegion().forEach((k, v) -> {
			config.getL1CacheProperties().setProperty(PREFIX_REGION + k, v);
		});

		//特殊处理L2配置
		config.getL2CacheProperties().setProperty("namespace", j2CacheConfigProps.getL2().getRedis().getNamespace());
		config.getL2CacheProperties().setProperty("storage", j2CacheConfigProps.getL2().getRedis().getStorage());
		config.getL2CacheProperties().setProperty("channel", j2CacheConfigProps.getL2().getRedis().getChannel());
		config.getL2CacheProperties().setProperty("database", String.valueOf(j2CacheConfigProps.getL2().getRedis().getDatabase()));


		return config;
	}

	/**
	 * 匹配指定前缀（且删除前缀）提取J2Cache配置属性
	 *
	 * @param sourceProps
	 * @param destProps
	 * @param prefix
	 */
	private static void matchPropsPrefixAndSet(Properties sourceProps, Properties destProps, String prefix) {
		sourceProps.forEach((k, v) -> {
			String key = (String) k;
			String value = String.valueOf(v);
			String prefixWithDot = prefix + DOT_STR;
			if (key.startsWith(prefixWithDot)) {
				destProps.setProperty(key.substring(prefixWithDot.length()), value);
			}
		});
	}


}
