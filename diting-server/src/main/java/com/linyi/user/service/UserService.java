package com.linyi.user.service;

import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.vo.response.user.UserInfoResp;

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
}
