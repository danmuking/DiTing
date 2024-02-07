package com.linyi.common.constant;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-07 20:33
 **/
public class MQConstant {
    /**
     * 消息发送mq
     */
    public static final String SEND_MSG_TOPIC = "chat_send_msg";
    public static final String SEND_MSG_GROUP = "chat_send_msg_group";

    /**
     * push用户
     */
    public static final String PUSH_TOPIC = "websocket_push";
    public static final String PUSH_GROUP = "websocket_push_group";

    /**
     * (授权完成后)登录信息mq
     */
    public static final String LOGIN_MSG_TOPIC = "user_login_send_msg";
    public static final String LOGIN_MSG_GROUP = "user_login_send_msg_group";

    /**
     * 扫码成功 信息发送mq
     */
    public static final String SCAN_MSG_TOPIC = "user_scan_send_msg";
    public static final String SCAN_MSG_GROUP = "user_scan_send_msg_group";
}
