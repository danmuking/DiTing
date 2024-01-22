package com.linyi.user.service.impl;

import com.linyi.common.domain.enums.NormalOrNoEnum;
import com.linyi.common.domain.enums.RoomTypeEnum;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.dao.RoomFriendDao;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.service.RoomService;
import com.linyi.user.service.adapter.RoomAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
