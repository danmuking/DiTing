package com.linyi.user.service.adapter;

import com.linyi.common.domain.enums.YesOrNoEnum;
import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserBackpack;
import com.linyi.user.domain.vo.response.user.BadgeResp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-17 15:44
 **/
public class BadgeRespAdapter {

    public static List<BadgeResp> bulidBadgeResp(List<ItemConfig> badges, List<UserBackpack> userBadges, User byUid) {
        ArrayList<BadgeResp> badgeResps = new ArrayList<>();
//        将用户拥有的徽章id放入集合
        Set<Long> obtains = userBadges.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
//        创建数组
        for(ItemConfig itemConfig:badges){
            BadgeResp badgeResp = new BadgeResp();
            badgeResp.setId(itemConfig.getId());
            badgeResp.setImg(itemConfig.getImg());
            badgeResp.setDescribe(itemConfig.getDescribe());
            badgeResp.setWearing(byUid.getItemId()==itemConfig.getId()? YesOrNoEnum.YES.getStatus() :YesOrNoEnum.NO.getStatus());
            badgeResp.setObtain(obtains.contains(itemConfig.getId())?YesOrNoEnum.YES.getStatus():YesOrNoEnum.NO.getStatus());
            badgeResps.add(badgeResp);
        }
        badgeResps.sort((o1, o2) -> {
//            拥有的徽章排在前面
            if(o1.getWearing().equals(o2.getWearing())){
                return o2.getObtain()-o1.getObtain();
            }
//            佩戴的徽章排在前面
            return o2.getWearing()-o1.getWearing();
        });
        return badgeResps;
    }
}
