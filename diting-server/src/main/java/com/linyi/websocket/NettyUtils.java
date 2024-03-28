package com.linyi.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @description netty工具类
 * @date 2024/1/8 19:58
 */
public class NettyUtils {
    public static AttributeKey<Long> UID = AttributeKey.valueOf("uid");
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    /**
     * @param channel: 要设置属性的通道
     * @param attributeKey: 属性
     * @param data: 数据
     * @return void
     * @description 给通道设置属性
     * @date 2024/1/8 19:59
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey,T data){
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }
    public static <T> T getAttr(Channel channel, AttributeKey<T> attributeKey){
        Attribute<T> attr = channel.attr(attributeKey);
        return attr.get();
    }
}
