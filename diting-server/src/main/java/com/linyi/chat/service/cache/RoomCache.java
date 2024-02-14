package com.linyi.chat.service.cache;

import com.linyi.common.constant.RedisKey;
import com.linyi.common.service.cache.AbstractJ2Cache;
import com.linyi.user.dao.RoomDao;
import com.linyi.user.domain.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-14 15:16
 **/
@Component
public class RoomCache extends AbstractJ2Cache<Long, Room> {
    @Autowired
    private RoomDao roomDao;
    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomDao.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
