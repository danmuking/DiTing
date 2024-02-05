package com.luo.j2cache.sb.cache.support.util;

import net.oschina.j2cache.util.SerializationUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * Spring Redis序列化J2Cache实现
 *
 * @author luohq
 * @date 2022-04-07
 */
public class J2CacheSerializer implements RedisSerializer<Object>{

	@Override
	public byte[] serialize(Object t) throws SerializationException {	
		try {
			return SerializationUtils.serialize(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		try {
			return SerializationUtils.deserialize(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
