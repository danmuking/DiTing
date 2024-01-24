package com.linyi.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.linyi.chat.dao.ContactDao;
import com.linyi.chat.dao.MessageDao;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.vo.request.ChatMessagePageReq;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.chat.service.ChatService;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.domain.entity.Room;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-24 20:56
 **/
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    ContactDao contactDao;
    @Autowired
    RoomDao roomDao;
    @Autowired
    MessageDao messageDao;
    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long uid) {
//        获取该用户能见的最后一条消息id，防止用户被踢出后能看见之后的消息
        Long lastMsgId = getLastMsgId(request.getRoomId(), uid);
//        获取消息列表
        CursorPageBaseResp<Message> cursorPage = messageDao.getCursorPage(request.getRoomId(), request, lastMsgId);
        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), uid));
    }

    private List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long uid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        //查询消息标志
//        List<ChatMessageResp.MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
//        return MessageAdapter.buildMsgResp(messages, msgMark, uid);
        return null;
    }

    /**
     * @param roomId:
     * @param uid:
     * @return Long
     * @description 获取用户最后一条可见消息id
     * @date 2024/1/24 21:05
     */
    private Long getLastMsgId(Long roomId, Long uid) {
//        对应房间号是否存在
        Room byId = roomDao.getById(roomId);
        AssertUtil.isNotEmpty(byId, "房间号有误");
//        热点房间所有人可见
        if (byId.isHotRoom()) {
            return null;
        }
        Contact contact = contactDao.get(uid, roomId);
        return contact==null ? 0 :contact.getLastMsgId();
    }
}
