package com.linyi.common.event;

import com.linyi.chat.domain.dto.ChatMsgRecallDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @program: DiTing
 * @description: 消息撤回事件
 * @author: lin
 * @create: 2024-01-26 18:24
 **/
@Getter
public class MessageRecallEvent extends ApplicationEvent {

    private final ChatMsgRecallDTO recallDTO;

    public MessageRecallEvent(Object source, ChatMsgRecallDTO recallDTO) {
        super(source);
        this.recallDTO = recallDTO;
    }

}

