package com.linyi.user.service.adapter;

import com.linyi.common.domain.vo.response.PageBaseResp;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserApply;
import com.linyi.user.domain.entity.UserFriend;
import com.linyi.user.domain.enums.ApplyReadStatusEnum;
import com.linyi.user.domain.enums.ApplyStatusEnum;
import com.linyi.user.domain.enums.ApplyTypeEnum;
import com.linyi.user.domain.vo.request.friend.FriendApplyReq;
import com.linyi.user.domain.vo.response.friend.FriendApplyResp;
import com.linyi.user.domain.vo.response.friend.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
    }

    public static List<FriendResp> buildFriend(List<UserFriend> list, List<User> userList) {
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return list.stream().map(userFriend -> {
            FriendResp resp = new FriendResp();
            resp.setUid(userFriend.getFriendUid());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
                resp.setActiveStatus(user.getActiveStatus());
            }
            return resp;
        }).collect(Collectors.toList());
    }
}
