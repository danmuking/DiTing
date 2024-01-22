package com.linyi.user.service.adapter;

import com.linyi.common.domain.enums.NormalOrNoEnum;
import com.linyi.common.domain.enums.RoomTypeEnum;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.domain.enums.HotFlagEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @package: com.linyi.user.service.adapter
 * @className: RoomAdapter
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/22 22:56
 * @version: 1.0
 */
public class RoomAdapter {
    private static final String SEPARATOR = ",";
    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }

    public static String generateRoomKey(List<Long> uidList) {
        return uidList.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }
    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setStatus(NormalOrNoEnum.NORMAL.getStatus());
        return roomFriend;
    }
}
