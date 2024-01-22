package com.linyi.user.service;

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
}
