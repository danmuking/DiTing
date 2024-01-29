package com.linyi.user.service.adapter;

import com.linyi.chat.domain.entity.RoomGroup;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.domain.entity.User;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-27 10:18
 **/
public class ChatAdapter {
    public static Set<Long> getFriendUidSet(Collection<RoomFriend> values, Long uid) {
        return values.stream()
                .map(a -> getFriendUid(a, uid))
                .collect(Collectors.toSet());
    }
    /**
     * 获取好友uid
     */
    public static Long getFriendUid(RoomFriend roomFriend, Long uid) {
        return Objects.equals(uid, roomFriend.getUid1()) ? roomFriend.getUid2() : roomFriend.getUid1();
    }

    public static RoomGroup buildGroupRoom(User user, Long roomId) {
        RoomGroup roomGroup = new RoomGroup();
        roomGroup.setName(user.getName() + "的群组");
        roomGroup.setAvatar(user.getAvatar());
        roomGroup.setRoomId(roomId);
        return roomGroup;
    }
}
