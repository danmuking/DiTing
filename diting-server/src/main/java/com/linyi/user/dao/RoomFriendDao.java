package com.linyi.user.dao;

import com.linyi.common.domain.enums.NormalOrNoEnum;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.mapper.RoomFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {
    public RoomFriend getByKey(String key) {
        return lambdaQuery().eq(RoomFriend::getRoomKey, key).one();
    }

    public void restoreRoom(Long id) {
        lambdaUpdate()
                .set(RoomFriend::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .eq(RoomFriend::getId, id)
                .update();
    }

    public void disableRoom(String roomKey) {
        lambdaUpdate()
                .set(RoomFriend::getStatus, NormalOrNoEnum.NOT_NORMAL.getStatus())
                .eq(RoomFriend::getRoomKey, roomKey)
                .update();
    }

    public RoomFriend getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomId, roomId)
                .one();
    }

    public List<RoomFriend> getBatchByIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomFriend::getRoomId, roomIds)
                .list();
    }
}
