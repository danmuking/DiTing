package com.linyi.user.service;


import com.linyi.user.domain.vo.request.user.WSAuthorize;
import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

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
    void handleLoginReq(Channel channel) throws WxErrorException;

    /**
     * @param channel:
     * @return void
     * @description 用户下线
     * @date 2024/1/11 19:16
     */
    void userOffline(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    void sendAuthorizeMsg(int code);

    /**
     * @param channel: ws通道
     * @param wsAuthorize: 用户token
     * @return void
     * @description 主动认证登录
     * @date 2024/1/14 22:45
     */
    void authorize(Channel channel, WSAuthorize wsAuthorize);
}
