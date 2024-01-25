package com.linyi.common.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @program: DiTing
 * @description: 消息发送事件
 * @author: lin
 * @create: 2024-01-25 20:05
 **/
public class MessageSendEvent extends ApplicationEvent {
    private Long msgId;
    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
