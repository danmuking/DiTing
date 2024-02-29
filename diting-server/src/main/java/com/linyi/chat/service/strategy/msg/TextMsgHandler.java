package com.linyi.chat.service.strategy.msg;

import com.linyi.chat.dao.MessageDao;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.entity.MessageExtra;
import com.linyi.chat.domain.enums.MessageStatusEnum;
import com.linyi.chat.domain.enums.MessageTypeEnum;
import com.linyi.chat.domain.vo.request.msg.TextMsgReq;
import com.linyi.chat.domain.vo.response.msg.TextMsgResp;
import com.linyi.chat.service.adapter.MessageAdapter;
import com.linyi.common.domain.enums.YesOrNoEnum;
import com.linyi.common.utils.discover.PrioritizedUrlDiscover;
import com.linyi.common.utils.discover.domain.UrlInfo;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: DiTing
 * @description: 普通文本消息
 * @author: lin
 * @create: 2024-01-25 18:45
 **/
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {
    @Autowired
    MessageDao messageDao;
    @Autowired
    UserDao userDao;

    /**
     * @param null:
     * @return null
     * @description Url连接解析器
     * @date 2024/2/29 23:38
     */
    private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();
    /**
     * @param :
     * @return MessageTypeEnum
     * @description 返回消息类型
     * @date 2024/1/25 18:46
     */
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    /**
     * @param message:
     * @param body:
     * @return void
     * @description 消息入库
     * @date 2024/1/25 18:48
     */
    @Override
    protected void saveMsg(Message message, TextMsgReq body) {
//        设置额外信息
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setContent(body.getContent());
        update.setExtra(extra);
//        如果当前消息有回复之前的消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Integer gapCount = messageDao.getGapCount(message.getRoomId(), body.getReplyMsgId(), message.getId());
            update.setGapCount(gapCount);
            update.setReplyMsgId(body.getReplyMsgId());
        }
        //判断消息url跳转
        Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
        extra.setUrlContentMap(urlContentMap);
//        入库
        messageDao.updateById(update);
    }

    /**
     * @param msg:
     * @return Object
     * @description 展示的消息
     * @date 2024/1/25 19:06
     */
    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setUrlContentMap(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getUrlContentMap).orElse(null));
//        resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
//        如果存在要回复的消息
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
//                根据id查询
                .map(messageDao::getById)
//                过滤掉状态不正常的消息
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
        if (reply.isPresent()){
//            装配被回复消息
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMsgVO.setType(replyMessage.getType());
            replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userDao.getByUid(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
//            设置是否允许跳转到回复消息位置
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(msg.getGapCount());
            resp.setReply(replyMsgVO);
        }
        return resp;
    }

    /**
     * 被回复时——展示的消息
     *
     * @param msg
     */
    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    /**
     * 会话列表——展示的消息
     *
     * @param msg
     */
    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
