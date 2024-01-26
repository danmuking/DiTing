package com.linyi.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.linyi.chat.dao.ContactDao;
import com.linyi.chat.dao.RoomGroupDao;
import com.linyi.chat.domain.dto.RoomBaseInfo;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.RoomGroup;
import com.linyi.chat.domain.vo.response.ChatRoomResp;
import com.linyi.common.domain.enums.NormalOrNoEnum;
import com.linyi.common.domain.enums.RoomTypeEnum;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.dao.RoomFriendDao;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.RoomService;
import com.linyi.user.service.adapter.RoomAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.linyi.user.service.adapter.RoomAdapter.generateRoomKey;

/**
 * @package: com.linyi.user.service.impl
 * @className: RoomServiceImpl
 * @author: Lin
 * @description: 聊天房间服务
 * @date: 2024/1/22 22:39
 * @version: 1.0
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private RoomGroupDao roomGroupDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RoomFriend createFriendRoom(List<Long> uidList) {
//        参数校验
//        list是否为空
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
//        list长度是否为2
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
//        生成唯一单聊房间标识
        String roomKey = generateRoomKey(uidList);
//        房间是否已存在
        RoomFriend roomFriend = roomFriendDao.getByKey(roomKey);
//        如果存在房间就恢复，适用于恢复好友场景
        if (Objects.nonNull(roomFriend)) { 
            restoreRoomIfNeed(roomFriend);
        }
//        新建房间
        else {
//            房间入库
            Room room = createRoom(RoomTypeEnum.FRIEND);
//            单聊入库
            roomFriend = createFriendRoom(room.getId(), uidList);
        }
        return roomFriend;
    }

    @Override
    public void disableFriendRoom(List<Long> list) {
//        排序，小在前，大在后
        List<Long> collect = list.stream().sorted().collect(Collectors.toList());
//        生成唯一单聊房间标识
        String roomKey = generateRoomKey(collect);
        roomFriendDao.disableRoom(roomKey);

    }

    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
        CursorPageBaseResp<Long> page;
//        uid不为空，获取个人会话列表
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

    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        // 表情和头像
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, uid);
        // 最后一条消息
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> messages = CollectionUtil.isEmpty(msgIds) ? new ArrayList<>() : messageDao.listByIds(msgIds);
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        Map<Long, User> lastMsgUidMap = userInfoCache.getBatch(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        // 消息未读数
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

    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        List<Room> roomList = roomDao.getBatchById(roomIds);
        Map<Long, Room> roomMap = roomList.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
//         房间根据群组类型分组
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群组信息
        List<Long> groupRoomId = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
        List<RoomGroup> batchById = roomGroupDao.getBatchById(groupRoomId);
        Map<Long, RoomGroup> roomInfoBatch = batchById.stream().collect(Collectors.toMap(RoomGroup::getId, Function.identity()));
        // 获取单聊信息
        List<Long> friendRoomId = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomId, uid);

        return roomMap.values().stream().map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setHotFlag(room.getHotFlag());
            roomBaseInfo.setLastMsgId(room.getLastMsgId());
            roomBaseInfo.setActiveTime(room.getActiveTime());
            if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                RoomGroup roomGroup = roomInfoBatch.get(room.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
            } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                User user = friendRoomMap.get(room.getId());
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
        List<RoomFriend> batchByIds = roomFriendDao.getBatchByIds(roomIds);
//        TODO: 看到这里
        Map<Long, RoomFriend> roomFriendMap = batchByIds.stream().map(roomFriend -> {
            if (Objects.equals(roomFriend.getUid1(), uid)) {
                roomFriend.setFriendUid(roomFriend.getUid2());
            } else {
                roomFriend.setFriendUid(roomFriend.getUid1());
            }
            return roomFriend;
        }).collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity())
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
        Map<Long, User> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
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

    /**
     * @param roomId:
     * @param uidList:
     * @return RoomFriend
     * @description 创建单聊房间
     * @date 2024/1/22 23:28
     */
    private RoomFriend createFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend insert = RoomAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(insert);
        return insert;
    }

    private Room createRoom(RoomTypeEnum roomTypeEnum) {
        Room insert = RoomAdapter.buildRoom(roomTypeEnum);
        roomDao.save(insert);
        return insert;
    }

    private void restoreRoomIfNeed(RoomFriend room) {
//        如果房间状态不正常，恢复房间
        if (Objects.equals(room.getStatus(), NormalOrNoEnum.NOT_NORMAL.getStatus())) {
            roomFriendDao.restoreRoom(room.getId());
        }
    }


}
