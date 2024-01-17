package com.linyi.user.service;

import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.vo.request.WearingBadgeReq;
import com.linyi.user.domain.vo.response.user.BadgeResp;
import com.linyi.user.domain.vo.response.user.UserInfoResp;

import java.util.List;

public interface UserService {
    /**
     * @param user:
     * @return void
     * @description 用户注册
     * @date 2024/1/11 18:56
     */
    public void register(User user);

    /**
     * @param uid:
     * @return Object
     * @description 获取用户信息
     * @date 2024/1/16 19:54
     */
    UserInfoResp getUserInfo(Long uid);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, WearingBadgeReq req);
}
