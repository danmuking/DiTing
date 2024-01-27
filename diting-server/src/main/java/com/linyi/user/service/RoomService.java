package com.linyi.user.service;

import com.linyi.chat.domain.vo.response.ChatRoomResp;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.user.domain.entity.Room;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.user.domain.entity.RoomFriend;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
public interface RoomService {

    RoomFriend createFriendRoom(List<Long> list);

    /**
     * @param list:
     * @return void
     * @description 禁用好友房间
     * @date 2024/1/23 19:15
     */
    void disableFriendRoom(List<Long> list);


}
