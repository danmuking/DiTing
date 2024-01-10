package com.linyi.user.service;


import io.netty.channel.Channel;

/**
 * @package: com.linyi.websocket.service
 * @className: WebSocketService
 * @author: Lin
 * @description: WebSocket服务
 * @date: 2024/1/10 23:06
 * @version: 1.0
 */
public interface WebSocketService {
    /**
     * @param channel:
     * @description 添加channel连接
     * @date 2024/1/10 23:09
     */
    public void connect(Channel channel);

    /**
     * @param channel:
     * @description 处理用户登录请求，需要返回一张带code的二维码
     * @date 2024/1/10 23:46
     */
    void handleLoginReq(Channel channel);
}
