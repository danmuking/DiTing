package com.linyi.user.service.adapter;

import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.vo.response.user.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.BeanUtils;

public class UserAdapter {

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user;
        user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        return user;
    }

    public static UserInfoResp buildUserInfoResp(User user, Integer renameCardNum) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtils.copyProperties(user,userInfoResp);
        userInfoResp.setModifyNameChance(renameCardNum);
        return userInfoResp;
    }
}
