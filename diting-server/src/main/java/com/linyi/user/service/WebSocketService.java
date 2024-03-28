package com.linyi.user.service;


import com.linyi.user.domain.vo.request.user.WSAuthorize;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
    void userOffline(ChannelHandlerContext channel);

    /**
     * @param code:
     * @param id:
     * @return void
     * @description 登录成功流程
     * @date 2024/1/30 18:03
     */
    void scanLoginSuccess(Integer code, Long id);

    /**
     * @param code:
     * @return void
     * @description 发送授权消息
     * @date 2024/1/30 18:03
     */
    void sendAuthorizeMsg(int code);

    /**
     * @param channel: ws通道
     * @param wsAuthorize: 用户token
     * @return void
     * @description 主动认证登录
     * @date 2024/1/14 22:45
     */
    void authorize(Channel channel, WSAuthorize wsAuthorize);

    void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid);

    void sendToAllOnline(WSBaseResp<?> wsBaseMsg, Long skipUid);

    Boolean scanSuccess(Integer code);
}
