package com.linyi.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * @package: com.linyi.websocket
 * @className: WebSocketConnector
 * @author: Lin
 * @description: 模拟客户端连接
 * @date: 2024/1/7 23:14
 * @version: 1.0
 */
@Slf4j
public class WebSocketConnector {
    private final String serverIp;
    private final int serverPort;
    private final EventLoopGroup group;
    // 网络通道
    private Channel channel;

    public WebSocketConnector(String serverIp, int serverPort, EventLoopGroup group) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.group = group;
    }

    public void doConnect() {
        URI uri = null;
        try {
            String url = "ws://" + this.serverIp + ":" + this.serverPort + "/";
            uri = new URI(url);
            final WebSocketIoHandler handler =
                    new WebSocketIoHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IdleStateHandler(0, 10, 0));
                            // 添加一个http的编解码器
                            pipeline.addLast(new HttpClientCodec());
                            // 添加一个用于支持大数据流的支持
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 添加一个聚合器，这个聚合器主要是将HttpMessage聚合成FullHttpRequest/Response
                            pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                            pipeline.addLast(handler);
                        }
                    });
            synchronized (bootstrap) {
                Channel channel = bootstrap.connect(serverIp, serverPort).sync().channel();
            }
        } catch (Exception e) {
            log.error("连接服务失败.......................", e);
        }
    }
//    断开连接
    public void close() {
        this.channel.close();
    }
//    销毁
    public void destroy() {
        group.shutdownGracefully();
        log.info("关闭 ws client 成功");
    }
}
