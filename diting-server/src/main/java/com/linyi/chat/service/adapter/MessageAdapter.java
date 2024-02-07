package com.linyi.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.entity.MessageMark;
import com.linyi.chat.domain.enums.MessageMarkTypeEnum;
import com.linyi.chat.domain.enums.MessageStatusEnum;
import com.linyi.chat.domain.enums.MessageTypeEnum;
import com.linyi.chat.domain.vo.request.ChatMessageReq;
import com.linyi.chat.domain.vo.request.msg.TextMsgReq;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.chat.service.strategy.msg.AbstractMsgHandler;
import com.linyi.chat.service.strategy.msg.MsgHandlerFactory;
import com.linyi.common.domain.enums.YesOrNoEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @package: com.linyi.chat.service.adapter
 * @className: MessageAdapter
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/24 22:39
 * @version: 1.0
 */
public class MessageAdapter {
    /**
     * @param null:
     * @return null
     * @description 回复消息允许跳转的最大间隔
     * @date 2024/1/25 19:16
     */
    public static final int CAN_CALLBACK_GAP_COUNT = 100;
    /**
     * @param messages:
     * @param msgMark:
     * @param uid:
     * @return List<ChatMessageResp>
     * @description 装配消息响应
     * @date 2024/1/25 18:31
     */
    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, List<MessageMark> msgMark, Long uid) {
//        创建消息id和消息标记的映射
        Map<Long, List<MessageMark>> markMap = msgMark.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream().map(a -> {
                    ChatMessageResp resp = new ChatMessageResp();
                    resp.setFromUser(buildFromUser(a.getFromUid()));
                    resp.setMessage(buildMessage(a, markMap.getOrDefault(a.getId(), new ArrayList<>()), uid));
                    return resp;
                })
                .sorted(Comparator.comparing(a -> a.getMessage().getSendTime()))//帮前端排好序，更方便它展示
                .collect(Collectors.toList());
    }
    /**
     * @param fromUid:
     * @return UserInfo
     * @description 装配用户信息
     * @date 2024/1/25 18:33
     */
    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }
    /**
     * @param message:
     * @param marks:
     * @param receiveUid:
     * @return Message
     * @description 装配消息信息
     * @date 2024/1/25 18:33
     */
    private static ChatMessageResp.Message buildMessage(Message message, List<MessageMark> marks, Long receiveUid) {
//        装配消息
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
//        采用策略模式，根据不同的消息类型采用不同的消息体
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        if (Objects.nonNull(msgHandler)) {
            messageVO.setBody(msgHandler.showMsg(message));
        }
//        装配消息标记
        messageVO.setMessageMark(buildMsgMark(marks, receiveUid));
        return messageVO;
    }
    /**
     * @param marks:
     * @param receiveUid:
     * @return MessageMark
     * @description 装配消息标记
     * @date 2024/1/25 18:35
     */
    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> marks, Long receiveUid) {
//        按照消息标记类型分组
        Map<Integer, List<MessageMark>> typeMap = marks.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> likeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.LIKE.getType(), new ArrayList<>());
        List<MessageMark> dislikeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(), new ArrayList<>());
//        装配消息标记
        ChatMessageResp.MessageMark mark = new ChatMessageResp.MessageMark();
//        统计点赞和点踩的数量
//        判断用户是否点赞和点踩
        mark.setLikeCount(likeMarks.size());
        mark.setUserLike(Optional.ofNullable(receiveUid).filter(uid -> likeMarks.stream().anyMatch(a -> Objects.equals(a.getUid(), uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        mark.setDislikeCount(dislikeMarks.size());
        mark.setUserDislike(Optional.ofNullable(receiveUid).filter(uid -> dislikeMarks.stream().anyMatch(a -> Objects.equals(a.getUid(), uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        return mark;
    }

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {

        return Message.builder()
                .fromUid(uid)
                .roomId(request.getRoomId())
                .type(request.getMsgType())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .build();

    }

    /**
     * @param roomId:
     * @return ChatMessageReq
     * @description 构建统一消息
     * @date 2024/2/7 21:42
     */
    public static ChatMessageReq buildAgreeMsg(Long roomId) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(roomId);
        chatMessageReq.setMsgType(MessageTypeEnum.TEXT.getType());
        TextMsgReq textMsgReq = new TextMsgReq();
        textMsgReq.setContent("我们已经成为好友了，开始聊天吧");
        chatMessageReq.setBody(textMsgReq);
        return chatMessageReq;
    }
}
