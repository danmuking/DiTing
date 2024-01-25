package com.linyi.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.enums.MessageStatusEnum;
import com.linyi.chat.domain.vo.request.ChatMessagePageReq;
import com.linyi.chat.mapper.MessageMapper;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.service.IMessageService;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    public CursorPageBaseResp<Message> getCursorPage(Long roomId, ChatMessagePageReq request, Long lastMsgId) {
        return CursorUtils.getCursorPageByMysql(this,request,wrapper->{
            wrapper.eq(Message::getRoomId,roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        },Message::getId);
    }

    /**
     * @param roomId:
     * @param replyMsgId:
     * @param id:
     * @return Integer
     * @description 计算在当前消息和回复的消息中间有多少条消息
     * @date 2024/1/25 19:02
     */
    public Integer getGapCount(Long roomId, Long replyMsgId, Long id) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, replyMsgId)
                .le(Message::getId, id)
                .count();
    }
}
