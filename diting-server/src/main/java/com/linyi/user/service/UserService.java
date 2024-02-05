package com.linyi.user.service;

import com.linyi.user.domain.dto.ItemInfoDTO;
import com.linyi.user.domain.dto.SummeryInfoDTO;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.vo.request.friend.ItemInfoReq;
import com.linyi.user.domain.vo.request.user.BlackReq;
import com.linyi.user.domain.vo.request.user.ModifyNameReq;
import com.linyi.user.domain.vo.request.user.SummeryInfoReq;
import com.linyi.user.domain.vo.request.user.WearingBadgeReq;
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

    /**
     * @param uid:
     * @return List<BadgeResp>
     * @description 获取用户徽章信息
     * @date 2024/1/17 17:06
     */
    List<BadgeResp> badges(Long uid);

    /**
     * @param uid:
     * @param req:
     * @return void
     * @description 佩戴徽章
     * @date 2024/1/17 17:07
     */
    void wearingBadge(Long uid, WearingBadgeReq req);

    /**
     * @param uid:
     * @param req:
     * @return void
     * @description 修改用户名
     * @date 2024/1/17 17:07
     */
    void modifyName(Long uid, ModifyNameReq req);

    /**
     * @param req:
     * @return void
     * @description 拉黑用户
     * @date 2024/1/21 23:00
     */
    void black(BlackReq req);

    /**
     * @param req:
     * @return List<SummeryInfoDTO>
     * @description 查询用户聚合信息
     * @date 2024/2/4 23:30
     */
    List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);
}
