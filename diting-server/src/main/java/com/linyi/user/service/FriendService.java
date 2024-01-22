package com.linyi.user.service;

import com.linyi.user.domain.entity.RoomFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.user.domain.vo.request.friend.FriendApplyReq;

/**
 * <p>
 * 单聊房间表 服务类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
public interface FriendService {

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 好友申请
     * @date 2024/1/22 22:00
     */
    void apply(Long uid, FriendApplyReq request);
}
