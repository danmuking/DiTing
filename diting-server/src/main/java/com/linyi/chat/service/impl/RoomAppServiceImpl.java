package com.linyi.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.linyi.chat.dao.ContactDao;
import com.linyi.chat.dao.GroupMemberDao;
import com.linyi.chat.dao.MessageDao;
import com.linyi.chat.dao.RoomGroupDao;
import com.linyi.chat.domain.dto.RoomBaseInfo;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.entity.RoomGroup;
import com.linyi.chat.domain.enums.GroupRoleAPPEnum;
import com.linyi.chat.domain.enums.GroupRoleEnum;
import com.linyi.chat.domain.vo.request.ChatMessageMemberReq;
import com.linyi.chat.domain.vo.request.GroupAddReq;
import com.linyi.chat.domain.vo.request.MemberDelReq;
import com.linyi.chat.domain.vo.request.MemberReq;
import com.linyi.chat.domain.vo.response.ChatMemberListResp;
import com.linyi.chat.domain.vo.response.ChatMemberResp;
import com.linyi.chat.domain.vo.response.ChatRoomResp;
import com.linyi.chat.domain.vo.response.MemberResp;
import com.linyi.chat.service.ChatService;
import com.linyi.chat.service.RoomAppService;
import com.linyi.chat.service.adapter.MemberAdapter;
import com.linyi.chat.service.adapter.RoomAdapter;
import com.linyi.chat.service.strategy.msg.AbstractMsgHandler;
import com.linyi.chat.service.strategy.msg.MsgHandlerFactory;
import com.linyi.common.domain.enums.RoomTypeEnum;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.event.GroupMemberAddEvent;
import com.linyi.common.exception.GroupErrorEnum;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.dao.RoomFriendDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.HotFlagEnum;
import com.linyi.user.domain.enums.RoleEnum;
import com.linyi.user.domain.vo.request.user.WSMemberChange;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import com.linyi.user.service.IRoleService;
import com.linyi.user.service.RoomService;
import com.linyi.user.service.adapter.ChatAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-27 10:30
 **/
@Service
public class RoomAppServiceImpl implements RoomAppService {
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private RoomService roomService;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private ChatService chatService;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
        CursorPageBaseResp<Long> page;
//        uid不为空，获取个人会话列表及热点会话
        if (Objects.nonNull(uid)) {
//            热点房间的结束位置
            Double hotEnd = getCursorOrNull(request.getCursor());
//            热点房间的开始位置需要以查询出的用户房间位置为准
            Double hotStart = null;
//             查询用户普通会话
            CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(uid, request);
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
//            如果不是最后一页，设置热点房间的开始位置
            if (!contactPage.getIsLast()) {
                hotStart = getCursorOrNull(contactPage.getCursor());
            }
//             查询位于用户房间游标之间的热门房间
            List<Room> hotRooms = roomDao.getRoomByRange(hotStart, hotEnd);
            List<Long> hotRoomIds = hotRooms.stream().map(Room::getId).filter(Objects::nonNull).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);
//             基础会话和热门房间合并
            page = CursorPageBaseResp.init(contactPage, baseRoomIds);
        }
//        用户未登录只能查看热点房间
        else {
            CursorPageBaseResp<Room> roomCursorPage = roomDao.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Room::getId).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }
//         最后组装会话信息（名称，头像，未读数等）
        List<ChatRoomResp> result = buildContactResp(uid, page.getList());
        return CursorPageBaseResp.init(page, result);
    }

    /**
     * @param uid
     * @param roomId
     * @return
     */
    @Override
    public ChatRoomResp getContactDetail(Long uid, long roomId) {
        Room room = roomDao.getById(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        return buildContactResp(uid, Collections.singletonList(roomId)).get(0);
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid) {
        RoomFriend friendRoom = roomService.getFriendRoom(uid, friendUid);
        AssertUtil.isNotEmpty(friendRoom, "他不是您的好友");
        return buildContactResp(uid, Collections.singletonList(friendRoom.getRoomId())).get(0);
    }

    @Override
    public MemberResp getGroupDetail(Long uid, long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        Room room = roomDao.getById(roomId);
        AssertUtil.isNotEmpty(roomGroup, "roomId有误");
//        TODO:热点群缓存
//        拉取在线人数
        Long onlineNum;
        List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
        onlineNum = userDao.getOnlineCount(memberUidList).longValue();
//        拉取用户角色
        GroupRoleAPPEnum groupRole = getGroupRole(uid, roomGroup, room);
        return MemberResp.builder()
                .avatar(roomGroup.getAvatar())
                .roomId(roomId)
                .groupName(roomGroup.getName())
                .onlineNum(onlineNum)
                .role(groupRole.getType())
                .build();
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request) {
        Room room = roomDao.getById(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        List<Long> memberUidList;
        if (isHotGroup(room)) {// 全员群展示所有用户
            memberUidList = null;
        }
        else {// 只展示房间内的群成员
            RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
            memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
        }
        return chatService.getMemberPage(memberUidList, request);
    }

    @Override
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request) {
//        获取房间信息
        Room room = roomDao.getById(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
//        全员群展示所有用户的前1000名
        if (isHotGroup(room)) {
            List<User> memberList = userDao.getMemberList();
            return MemberAdapter.buildMemberList(memberList);
        } else {
//            获取群聊信息
            RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
//            获取群成员列表
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
            List<User> users = userDao.getBatchByIds(memberUidList);
            return MemberAdapter.buildMemberList(users);
        }
    }

    @Override
    public void delMember(Long uid, MemberDelReq request) {
//        获取房间信息
        Room room = roomDao.getById(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
//        获取群聊信息
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "房间号有误");
//        获取用户信息
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
//        用户不存在，报错
        AssertUtil.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP);
//        权限校验
//        1. 判断被移除的人是否是群主或者管理员  （群主不可以被移除，管理员只能被群主移除）
        Long removedUid = request.getUid();
//        1.1 群主 非法操作
        AssertUtil.isFalse(groupMemberDao.isLord(roomGroup.getId(), removedUid), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
//        1.2 管理员 判断是否是群主操作
        if (groupMemberDao.isManager(roomGroup.getId(), removedUid)) {
            Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
            AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        }
//         1.3 普通成员 判断是否有权限操作
        AssertUtil.isTrue(hasPower(self), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember member = groupMemberDao.getMember(roomGroup.getId(), removedUid);
        AssertUtil.isNotEmpty(member, "用户已经移除");
        groupMemberDao.removeById(member.getId());
//        发送移除事件告知群成员
        List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getRoomId());
        WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), member.getUid());
//        pushService.sendPushMsg(ws, memberUidList);
    }

    @Override
    public Long addGroup(Long uid, GroupAddReq request) {
        RoomGroup roomGroup = roomService.createGroupRoom(uid);
//        批量保存群成员
        List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(request.getUidList(), roomGroup.getId());
        groupMemberDao.saveBatch(groupMembers);
        // 发送邀请加群消息==》触发每个人的会话
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
        return roomGroup.getRoomId();
    }

    private boolean hasPower(GroupMember self) {
//        群主和管理员有权限，超级管理员也有权限
        return GroupRoleEnum.ADMIN_LIST.contains(self.getRole())|| iRoleService.hasPower(self.getUid(), RoleEnum.ADMIN);
    }

    /**
     * @param uid:
     * @param roomGroup:
     * @param room:
     * @return GroupRoleAPPEnum
     * @description 获取用户群聊角色
     * @date 2024/1/28 21:24
     */
    private GroupRoleAPPEnum getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
        GroupMember member = Objects.isNull(uid) ? null : groupMemberDao.getMember(roomGroup.getId(), uid);
//        拉取用户在群聊中的角色
        if (Objects.nonNull(member)) {
            return GroupRoleAPPEnum.of(member.getRole());
        }
//       热点群聊，没有设计就是成员
        else if (isHotGroup(room)) {
            return GroupRoleAPPEnum.MEMBER;
        }
//        否则就是被移除
        else {
            return GroupRoleAPPEnum.REMOVE;
        }
    }

    private boolean isHotGroup(Room room) {
        return HotFlagEnum.YES.getType().equals(room.getHotFlag());
    }

    /**
     * @param uid:
     * @param roomIds:
     * @return List<ChatRoomResp>
     * @description 组装会话信息
     * @date 2024/1/27 9:49
     */
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
//         名称和头像
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, uid);
//         会话的最后一条消息Id
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
//         获取会话的最后一条消息
        List<Message> messages = CollectionUtil.isEmpty(msgIds) ? new ArrayList<>() : messageDao.listByIds(msgIds);
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
//        获取会话的最后一条消息的发送者信息
        List<User> users = userDao.getBatchByIds(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        Map<Long, User> lastMsgUidMap =users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
//        获取会话未读数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(uid, roomIds);
        return roomBaseInfoMap.values().stream().map(room -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(room.getRoomId());
                    resp.setAvatar(roomBaseInfo.getAvatar());
                    resp.setRoomId(room.getRoomId());
                    resp.setActiveTime(room.getActiveTime());
                    resp.setHot_Flag(roomBaseInfo.getHotFlag());
                    resp.setType(roomBaseInfo.getType());
                    resp.setName(roomBaseInfo.getName());
                    Message message = msgMap.get(room.getLastMsgId());
                    if (Objects.nonNull(message)) {
                        AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        resp.setText(lastMsgUidMap.get(message.getFromUid()).getName() + ":" + strategyNoNull.showContactMsg(message));
                    }
                    resp.setUnreadCount(unReadCountMap.getOrDefault(room.getRoomId(), 0));
                    return resp;
                }).sorted(Comparator.comparing(ChatRoomResp::getActiveTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * @param uid:
     * @param roomIds:
     * @return Map<Long,Integer>
     * @description 获取消息未读数
     * @date 2024/1/27 10:06
     */
    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
//        获取对应房间用户的会话状态
        List<Contact> contacts = contactDao.getByRoomIds(roomIds, uid);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    /**
     * @param roomIds:
     * @param uid:
     * @return Map<Long,RoomBaseInfo>
     * @description 会话名称和头像，群组为会话名称和头像，单聊为好友名称和头像
     * @date 2024/1/27 9:50
     */
    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        List<Room> roomList = roomDao.getBatchById(roomIds);
        Map<Long, Room> roomMap = roomList.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
//         房间根据群组类型分组
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));
//         获取群组信息
        Map<Long, RoomGroup> roomInfoBatch = null;
        if(Objects.nonNull(groupRoomIdMap.get(RoomTypeEnum.GROUP.getType()))){
            List<Long> groupRoomId = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
            List<RoomGroup> batchById = roomGroupDao.getBatchById(groupRoomId);
            roomInfoBatch = Optional.ofNullable(batchById).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(RoomGroup::getId, Function.identity()));
        }
//         获取单聊信息
        Map<Long, User> friendRoomMap = null;
        if(Objects.nonNull(groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType()))){
            List<Long> friendRoomId = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
            friendRoomMap = getFriendRoomMap(friendRoomId, uid);
        }

        Map<Long, RoomGroup> finalRoomInfoBatch = roomInfoBatch;
        Map<Long, User> finalfriendRoomMap = friendRoomMap;
        return roomMap.values().stream().map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setHotFlag(room.getHotFlag());
            roomBaseInfo.setLastMsgId(room.getLastMsgId());
            roomBaseInfo.setActiveTime(room.getActiveTime());
//            群聊设置群聊名称和头像
            if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                RoomGroup roomGroup = finalRoomInfoBatch.get(room.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
            }
//            单聊设置好友名称和头像
            else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                User user = finalfriendRoomMap.get(room.getId());
                roomBaseInfo.setName(user.getName());
                roomBaseInfo.setAvatar(user.getAvatar());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
        if (CollectionUtil.isEmpty(roomIds)) {
            return new HashMap<>();
        }
//        查询单聊房间信息
        List<RoomFriend> batchByIds = roomFriendDao.getBatchByIds(roomIds);
        Map<Long, RoomFriend> roomFriendMap = Optional.ofNullable(batchByIds).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
//        获取好友uid
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
//        查询好友信息
        List<User> users = userDao.getBatchByIds(new ArrayList<>(friendUidSet));
        Map<Long, User> userBatch = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
//        将uid->user的映射转换为roomId->user的映射
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
                    Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));
    }

    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }
}
