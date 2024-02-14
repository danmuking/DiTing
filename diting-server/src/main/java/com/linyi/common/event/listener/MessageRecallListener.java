package com.linyi.common.event.listener;

import com.linyi.chat.domain.dto.ChatMsgRecallDTO;
import com.linyi.common.event.MessageRecallEvent;
import com.linyi.common.service.PushService;
import com.linyi.common.service.cache.MsgCache;
import com.linyi.user.service.adapter.WSAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @program: DiTing
 * @description: 消息撤回监听器
 * @author: lin
 * @create: 2024-02-14 16:32
 **/
@Component
public class MessageRecallListener {
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushService.sendPushMsg(WSAdapter.buildMsgRecall(event.getRecallDTO()));
    }
}
