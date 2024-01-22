package com.linyi.user.service.adapter;

import com.linyi.user.domain.entity.UserApply;
import com.linyi.user.domain.enums.ApplyReadStatusEnum;
import com.linyi.user.domain.enums.ApplyStatusEnum;
import com.linyi.user.domain.enums.ApplyTypeEnum;
import com.linyi.user.domain.vo.request.friend.FriendApplyReq;

/**
 * @package: com.linyi.user.service.adapter
 * @className: FriendAdapter
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/22 22:16
 * @version: 1.0
 */
public class FriendAdapter {
    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        return UserApply.builder()
                .uid(uid)
                .targetId(request.getTargetUid())
                .msg(request.getMsg())
                .type(ApplyTypeEnum.ADD_FRIEND.getCode())
                .status(ApplyStatusEnum.WAIT_APPROVAL.getCode())
                .readStatus(ApplyReadStatusEnum.UNREAD.getCode())
                .build();
    }
}
