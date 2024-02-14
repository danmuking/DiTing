package com.linyi.user.dao;

import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
import com.linyi.user.domain.entity.Room;
import com.linyi.user.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

    public List<Room> getRoomByRange(Double hotStart, Double hotEnd) {
        return lambdaQuery()
                .ge(Room::getActiveTime, hotStart)
                .lt(Room::getActiveTime, hotEnd)
                .list();
    }

    public CursorPageBaseResp<Room> getRoomCursorPage(CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.orderByDesc(Room::getActiveTime);
        }, Room::getActiveTime);
    }

    public List<Room> getBatchById(List<Long> roomIds) {
        return lambdaQuery()
                .in(Room::getId, roomIds)
                .list();
    }

    /**
     * @param roomId:
     * @param msgId:
     * @param msgTime:
     * @return void
     * @description 刷新房间活跃信息
     * @date 2024/2/14 15:56
     */
    public void refreshActiveTime(Long roomId, Long msgId, Date msgTime) {
        lambdaUpdate()
                .eq(Room::getId, roomId)
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, msgTime)
                .update();
    }
}
