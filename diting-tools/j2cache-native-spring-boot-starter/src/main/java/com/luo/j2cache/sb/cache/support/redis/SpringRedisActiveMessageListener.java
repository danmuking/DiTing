package com.luo.j2cache.sb.cache.support.redis;

import net.oschina.j2cache.cluster.ClusterPolicy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 监听二缓key失效，主动清除本地缓存
 *
 * @author luohq
 * @date 2022-04-07 09:34
 */
public class SpringRedisActiveMessageListener implements MessageListener {

	/**
	 * J2Cache广播实现
	 */
    private ClusterPolicy clusterPolicy;
	/**
	 * 命名空间，即对应region名称
	 */
    private String namespace;

    SpringRedisActiveMessageListener(ClusterPolicy clusterPolicy, String namespace) {
        this.clusterPolicy = clusterPolicy;
        this.namespace = namespace;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (key == null) {
            return;
        }
        if (key.startsWith(namespace + ":")) {
            String[] k = key.replaceFirst(namespace + ":", "").split(":", 2);
            if (k.length != 2) {
                return;
            }
            clusterPolicy.evict(k[0], k[1]);
        }

    }

}
