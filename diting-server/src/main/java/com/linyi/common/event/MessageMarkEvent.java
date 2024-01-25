package com.linyi.common.event;

import com.linyi.chat.domain.dto.ChatMessageMarkDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @package: com.linyi.common.event
 * @className: MessageMarkEvent
 * @author: Lin
 * @description: 消息标记事件
 * @date: 2024/1/25 22:41
 * @version: 1.0
 */
public class MessageMarkEvent extends ApplicationEvent {
    private ChatMessageMarkDTO chatMessageMarkDTO;
    public MessageMarkEvent(Object source,ChatMessageMarkDTO chatMessageMarkDTO){
        super(source);
        this.chatMessageMarkDTO = chatMessageMarkDTO;
    }
}
