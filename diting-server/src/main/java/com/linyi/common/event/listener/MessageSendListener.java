package com.linyi.common.event.listener;

import com.abin.mallchat.transaction.service.MQProducer;
import com.linyi.common.constant.MQConstant;
import com.linyi.common.domain.dto.MsgSendMessageDTO;
import com.linyi.common.event.MessageSendEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @program: DiTing
 * @description: 消息发送监听器
 * @author: lin
 * @create: 2024-02-08 23:30
 **/
@Slf4j
@Component
public class MessageSendListener {
    @Autowired
    private MQProducer mqProducer;
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }
}
