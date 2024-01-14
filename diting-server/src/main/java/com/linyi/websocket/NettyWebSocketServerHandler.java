package com.linyi.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.linyi.common.utils.JwtUtils;
import com.linyi.user.domain.enums.WSReqTypeEnum;
import com.linyi.user.domain.vo.request.WSAuthorize;
import com.linyi.user.domain.vo.request.WSBaseReq;
import com.linyi.user.service.WebSocketService;
import com.mysql.cj.util.StringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    private WebSocketService webSocketService;

    /**
     * @param ctx:
     * @return void
     * @description 通道就绪事件
     * @date 2024/1/6 11:03
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(String.format("%s 通道就绪", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(String.format("%s 通道关闭", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()));
        webSocketService.userOffline(ctx.channel());
    }

    /**
     * @param ctx:
     * @param evt:
     * @return void
     * @description 心跳检查
     * @date 2024/1/6 11:24
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        处理读空闲
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info(String.format("%s 通道读空闲,关闭通道", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()));
                webSocketService.userOffline(ctx.channel());
            }
        }
//        处理握手完毕事件
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info(String.format("%s 通道握手成功", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()));
//            取出token
            String token = ctx.channel().attr(NettyUtils.TOKEN).get();
//            如果token不为空就不需要再次登录
            if (StringUtil.isNotBlank(token)) {
//                关联通道和用户
                webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn(String.format("%s 通道异常,异常消息=%s", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()),cause.toString());
        ctx.channel().close();
    }

    /**
     * @param ctx:
     * @return void
     * @description 通道建立处理
     * @date 2024/1/10 22:42
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(String.format("%s 通道连接", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()));
        this.webSocketService = getService();
//        建立通道后，将通道加入到通道管理器中
        this.webSocketService.connect(ctx.channel());
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info(String.format("%s 通道关闭", ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
//        解析ws帧
        WSBaseReq wsBaseReq = JSONUtil.toBean(textWebSocketFrame.text(), WSBaseReq.class);
//        获取请求类型
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                this.webSocketService.handleLoginReq(channelHandlerContext.channel());
                break;
            case HEARTBEAT:
                break;
            default:
                break;
        }
    }
}
