package com.linyi.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @description Http处理器
 * @date 2024/1/8 20:18
 */
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        这个if只会执行一次,用于获取ip和token
        if(msg instanceof FullHttpRequest){
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(fullHttpRequest.uri());

//            获取token参数
            String token = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("token")).map(CharSequence::toString).orElse("");
            NettyUtils.setAttr(ctx.channel(), NettyUtils.TOKEN,token);
//            移除ws参数
            fullHttpRequest.setUri(urlBuilder.getPath().toString());

            HttpHeaders headers = fullHttpRequest.headers();
            String ip = headers.get("X-Real-IP");
            if (StringUtils.isEmpty(ip)) {//如果没经过nginx，就直接获取远端地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtils.setAttr(ctx.channel(), NettyUtils.IP, ip);
            ctx.pipeline().remove(this);
//            将request交给下一个handler
            ctx.fireChannelRead(fullHttpRequest);
        }else{
            ctx.fireChannelRead(msg);
        }
    }
}
