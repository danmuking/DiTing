package com.linyi.chat.service.cache;

import com.linyi.common.constant.RedisKey;
import com.linyi.common.service.cache.AbstractJ2Cache;
import com.linyi.user.dao.RoomFriendDao;
import com.linyi.user.domain.entity.RoomFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-14 15:23
 **/
@Component
public class RoomFriendCache extends AbstractJ2Cache<Long, RoomFriend> {
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Override
    protected String getKey(Long req) {
        return RedisKey.getKey(RedisKey.ROOM_FRIENDS, req);
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> req) {
        return roomFriendDao.getBatchByRoomIds(req).stream().collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> roomFriend));
    }
}
