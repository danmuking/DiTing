package com.linyi.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.RoomGroup;
import com.linyi.chat.service.IRoomGroupService;
import com.linyi.chat.mapper.RoomGroupMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }

    public List<RoomGroup> getBatchByRoomIds(List<Long> groupRoomId) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, groupRoomId)
                .list();
    }
}
