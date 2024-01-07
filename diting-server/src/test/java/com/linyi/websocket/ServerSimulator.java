package com.linyi.websocket;

/**
 * @package: com.linyi.websocket
 * @className: ServerSimulator
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/7 23:32
 * @version: 1.0
 */
public class ServerSimulator {
    public static void main(String[] args) throws InterruptedException {
        new NettyWebSocketServer().start();
    }
}
