package com.linyi.chat.service.cache;

import com.linyi.chat.dao.RoomGroupDao;
import com.linyi.chat.domain.entity.RoomGroup;
import com.linyi.common.constant.RedisKey;
import com.linyi.common.service.cache.AbstractJ2Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-14 15:30
 **/
@Component
public class RoomGroupCache extends AbstractJ2Cache<Long, RoomGroup> {
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Override
    protected String getKey(Long req) {
        return RedisKey.getKey(RedisKey.ROOM_GROUP, req);
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> req) {
        return roomGroupDao.getBatchByRoomIds(req).stream().collect(Collectors.toMap(RoomGroup::getRoomId, roomGroup -> roomGroup));
    }
}
