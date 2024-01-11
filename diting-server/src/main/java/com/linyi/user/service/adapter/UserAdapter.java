package com.linyi.user.service.adapter;

import com.linyi.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

public class UserAdapter {

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user;
        user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        return user;
    }
}
