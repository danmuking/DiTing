package com.linyi.chat.consumer;

import com.linyi.chat.dao.ContactDao;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.chat.service.ChatService;
import com.linyi.chat.service.cache.*;
import com.linyi.common.constant.MQConstant;
import com.linyi.common.domain.dto.MsgSendMessageDTO;
import com.linyi.common.domain.enums.RoomTypeEnum;
import com.linyi.common.service.PushService;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.service.adapter.WSAdapter;
import io.swagger.annotations.Api;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @program: DiTing
 * @description: 消息消费者
 * @author: lin
 * @create: 2024-02-14 15:43
 **/
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {
    @Autowired
    private MessageCache messageCache;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private PushService pushService;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private RoomFriendCache roomFriendCache;
    @Autowired
    private ContactDao contactDao;
    @Override
    public void onMessage(MsgSendMessageDTO msgSendMessageDTO) {
        Message message = messageCache.get(msgSendMessageDTO.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);
//        更新房间信息
        roomDao.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
        roomCache.delete(room.getId());
//        热门群聊推送所有在线的人
        if (room.isHotRoom()) {
            //更新热门群聊时间-redis
            hotRoomCache.refreshActiveTime(room.getId(), message.getCreateTime());
            //推送所有人
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp));
        }
        else {
            List<Long> memberUidList = new ArrayList<>();
            if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) {//普通群聊推送所有群成员
                memberUidList = groupMemberCache.getMemberUidList(room.getId());
            }
            else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())) {//单聊对象
                //对单人推送
                RoomFriend roomFriend = roomFriendCache.get(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            //更新所有群成员的会话时间
            contactDao.refreshOrCreateActiveTime(room.getId(), memberUidList, message.getId(), message.getCreateTime());
            //推送房间成员
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);
        }
    }
}
