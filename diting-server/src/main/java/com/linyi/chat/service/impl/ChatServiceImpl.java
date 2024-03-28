package com.linyi.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import com.linyi.chat.dao.*;
import com.linyi.chat.domain.dto.MsgReadInfoDTO;
import com.linyi.chat.domain.entity.*;
import com.linyi.chat.domain.enums.MessageMarkActTypeEnum;
import com.linyi.chat.domain.enums.MessageReadTypeEnum;
import com.linyi.chat.domain.enums.MessageTypeEnum;
import com.linyi.chat.domain.vo.request.*;
import com.linyi.chat.domain.vo.response.ChatMemberResp;
import com.linyi.chat.domain.vo.response.ChatMemberStatisticResp;
import com.linyi.chat.domain.vo.response.ChatMessageReadResp;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.chat.service.ChatService;
import com.linyi.chat.service.ContactService;
import com.linyi.chat.service.adapter.MemberAdapter;
import com.linyi.chat.service.adapter.MessageAdapter;
import com.linyi.chat.service.adapter.RoomAdapter;
import com.linyi.chat.service.cache.GroupMemberCache;
import com.linyi.chat.service.cache.RoomCache;
import com.linyi.chat.service.cache.RoomFriendCache;
import com.linyi.chat.service.cache.RoomGroupCache;
import com.linyi.chat.service.helper.ChatMemberHelper;
import com.linyi.chat.service.strategy.mark.AbstractMsgMarkStrategy;
import com.linyi.chat.service.strategy.mark.MsgMarkFactory;
import com.linyi.chat.service.strategy.msg.AbstractMsgHandler;
import com.linyi.chat.service.strategy.msg.MsgHandlerFactory;
import com.linyi.chat.service.strategy.msg.RecallMsgHandler;
import com.linyi.common.domain.enums.NormalOrNoEnum;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.event.MessageSendEvent;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.dao.RoomFriendDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.ChatActiveStatusEnum;
import com.linyi.user.domain.enums.RoleEnum;
import com.linyi.user.service.IRoleService;
import com.linyi.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    MessageMarkDao messageMarkDao;
    @Autowired
    RoomFriendDao roomFriendDao;
    @Autowired
    RoomGroupDao roomGroupDao;
    @Autowired
    GroupMemberDao groupMemberDao;
    @Autowired
    IRoleService iRoleService;
    @Autowired
    RecallMsgHandler recallMsgHandler;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ContactService contactService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private RoomFriendCache roomFriendCache;
    @Autowired
    private RoomGroupCache roomGroupCache;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private UserCache userCache;

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

    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
//        检查是否能发送消息
        check(request, uid);
//        采用策略模式处理消息
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
//        处理消息
        Long msgId = msgHandler.checkAndSaveMsg(request, uid);
//        发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long uid) {
        Message msg = messageDao.getById(msgId);
        return getMsgResp(msg, uid);
    }

    @Override
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())) {
            case MARK:
                strategy.mark(uid, request.getMsgId());
                break;
            case UN_MARK:
                strategy.unMark(uid, request.getMsgId());
                break;
        }
    }

    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        Message message = messageDao.getById(request.getMsgId());
//        校验是否有权限撤回
        checkRecall(uid, message);
//        撤回消息
        recallMsgHandler.recall(uid, message);
    }

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, ChatMessageReadReq request) {
//        获取消息信息
        Message message = messageDao.getById(request.getMsgId());
        AssertUtil.isNotEmpty(message, "消息id有误");
        AssertUtil.equal(uid, message.getFromUid(), "只能查看自己的消息");
        CursorPageBaseResp<Contact> page;
//        查询已读用户
        if (MessageReadTypeEnum.READ.getType() == request.getSearchType()) {//已读
            page = contactDao.getReadPage(message, request);
        }
//        查询未读用户
        else {
            page = contactDao.getUnReadPage(message, request);
        }
        if (CollectionUtil.isEmpty(page.getList())) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(page, RoomAdapter.buildReadResp(page.getList()));
    }

    @Override
    public Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request) {
        List<Message> messages = messageDao.listByIds(request.getMsgIds());
        messages.forEach(message -> {
            AssertUtil.equal(uid, message.getFromUid(), "只能查询自己发送的消息");
        });
        return contactService.getMsgReadInfo(messages).values();
    }

    @Override
    public void msgRead(Long uid, ChatMessageMemberReq request) {
        Contact contact = contactDao.get(uid, request.getRoomId());
        if (Objects.nonNull(contact)) {
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactDao.updateById(update);
        } else {
            Contact insert = new Contact();
            insert.setUid(uid);
            insert.setRoomId(request.getRoomId());
            insert.setReadTime(new Date());
            contactDao.save(insert);
        }
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request) {
//        获取用户状态及时间游标
        Pair<ChatActiveStatusEnum, String> pair = ChatMemberHelper.getCursorPair(request.getCursor());
        ChatActiveStatusEnum activeStatusEnum = pair.getKey();
        String timeCursor = pair.getValue();
//        结果列表
        List<ChatMemberResp> resultList = new ArrayList<>();
        Boolean isLast = Boolean.FALSE;
        if (activeStatusEnum == ChatActiveStatusEnum.ONLINE) {
//            在线用户页
            CursorPageBaseResp<User> cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(request.getPageSize(), timeCursor), ChatActiveStatusEnum.ONLINE);
//            添加到结果列表
            resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));
//            如果是最后一页,从离线列表再补点数据
            if (cursorPage.getIsLast()) {
                activeStatusEnum = ChatActiveStatusEnum.OFFLINE;
                Integer leftSize = request.getPageSize() - cursorPage.getList().size();
                cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(leftSize, null), ChatActiveStatusEnum.OFFLINE);
//                补充离线用户
                resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));
            }
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        }
//        离线用户列表
        else if (activeStatusEnum == ChatActiveStatusEnum.OFFLINE) {
            CursorPageBaseResp<User> cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(request.getPageSize(), timeCursor), ChatActiveStatusEnum.OFFLINE);
//            添加离线用户
            resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        }
//        获取用户角色
        List<Long> uidList = resultList.stream().map(ChatMemberResp::getUid).collect(Collectors.toList());
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        Map<Long, Integer> uidMapRole = groupMemberDao.getMemberMapRole(roomGroup.getId(), uidList);
        resultList.forEach(member -> member.setRoleId(uidMapRole.get(member.getUid())));
        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(activeStatusEnum, timeCursor), isLast, resultList);
    }

    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息有误");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "消息无法撤回");
//        拥有管理权限,可以撤回
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if (hasPower) {
            return;
        }
//        消息发送者可以撤回
        boolean self = Objects.equals(uid, message.getFromUid());
        AssertUtil.isTrue(self, "抱歉,您没有权限");
//        消息发送时间小于2分钟可以撤回
        long between = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "覆水难收，超过2分钟的消息不能撤回哦~~");
    }

    public ChatMessageResp getMsgResp(Message msg, Long uid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(msg), uid));
    }

    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
//        System.out.println(Thread.currentThread().getName());
        Long onlineNum = userCache.getOnlineNum();
//        Long offlineNum = userCache.getOfflineNum();不展示总人数
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
//        resp.setTotalNum(onlineNum + offlineNum);
        return resp;
    }

    /**
     * @param request:
     * @param uid:
     * @return void
     * @description 校验是否能发送消息
     * @date 2024/1/25 19:52
     */
    private void check(ChatMessageReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
//        热点房间所有人可用
        if (room.isHotRoom()) {
            return;
        }
//        单聊
        if (room.isRoomFriend()) {
//            找到对应房间
            RoomFriend roomFriend = roomFriendCache.get(request.getRoomId());
//            判断房间状态
            AssertUtil.equal(NormalOrNoEnum.NORMAL.getStatus(), roomFriend.getStatus(), "您已经被对方拉黑");
//            判断双方好友关系
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "您已经被对方拉黑");
        }
//        群聊
        if (room.isRoomGroup()) {
//            判断用户是否在群中
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            GroupMember member = groupMemberCache.getMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(member, "您已经被移除该群");
        }

    }

    /**
     * @param messages:
     * @param uid:
     * @return List<ChatMessageResp>
     * @description 批量获取消息响应
     * @date 2024/1/25 18:31
     */
    private List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long uid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        //查询对应的消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, msgMark, uid);
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
