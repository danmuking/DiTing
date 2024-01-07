package com.linyi.websocket;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Simulator {
    public static void start() throws InterruptedException {
        String serverIp = "127.0.0.1";
        int serverPort = 8090;
        EventLoopGroup group = new NioEventLoopGroup();
        for (int i = 0; i < 1; i++) {
            WebSocketConnector client = new WebSocketConnector(serverIp, serverPort, group);
            client.doConnect();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        start();
    }
}
