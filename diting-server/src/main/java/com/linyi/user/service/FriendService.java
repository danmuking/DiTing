package com.linyi.user.service;

import com.linyi.common.domain.vo.request.PageBaseReq;
import com.linyi.common.domain.vo.response.PageBaseResp;
import com.linyi.user.domain.entity.RoomFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.user.domain.vo.request.friend.FriendApplyReq;
import com.linyi.user.domain.vo.response.friend.FriendApplyResp;
import com.linyi.user.domain.vo.response.friend.FriendApproveReq;
import com.linyi.user.domain.vo.response.friend.FriendUnreadResp;

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

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 同意好友申请
     * @date 2024/1/23 19:03
     */
    void applyApprove(Long uid, FriendApproveReq request);

    /**
     * @param uid:
     * @param targetUid:
     * @return void
     * @description 删除好友
     * @date 2024/1/23 19:06
     */
    void deleteFriend(Long uid, Long targetUid);

    /**
     * @param uid:
     * @param request:
     * @return Object
     * @description 分页返回好友申请列表
     * @date 2024/1/23 19:38
     */
    PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request);

    /**
     * @param uid:
     * @return FriendUnreadResp
     * @description 好友申请为读数
     * @date 2024/1/23 19:56
     */
    FriendUnreadResp unread(Long uid);
}
