package com.linyi.chat.service.strategy.msg;

import com.linyi.chat.dao.MessageDao;
import com.linyi.chat.domain.dto.ChatMsgRecallDTO;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.entity.MessageExtra;
import com.linyi.chat.domain.entity.MsgRecall;
import com.linyi.chat.domain.enums.MessageTypeEnum;
import com.linyi.common.event.MessageRecallEvent;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @program: DiTing
 * @description: 消息撤回
 * @author: lin
 * @create: 2024-01-26 18:13
 **/
@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object>{
    @Autowired
    private UserDao userDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    /**
     * 消息类型
     */
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    /**
     * @param message
     * @param body
     */
    @Override
    protected void saveMsg(Message message, Object body) {
        throw new UnsupportedOperationException();
    }

    /**
     * 展示消息
     *
     * @param msg
     */
    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userDao.getByUid(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    /**
     * 被回复时——展示的消息
     *
     * @param msg
     */
    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    /**
     * 会话列表——展示的消息
     *
     * @param msg
     */
    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }

    public void recall(Long recallUid, Message message) {//todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));
    }
}
