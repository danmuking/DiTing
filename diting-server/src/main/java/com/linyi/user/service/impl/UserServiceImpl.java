package com.linyi.user.service.impl;

import com.linyi.user.dao.ItemConfigDao;
import com.linyi.user.dao.UserBackpackDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserBackpack;
import com.linyi.user.domain.enums.ItemEnum;
import com.linyi.user.domain.enums.ItemTypeEnum;
import com.linyi.user.domain.vo.response.user.BadgeResp;
import com.linyi.user.domain.vo.response.user.UserInfoResp;
import com.linyi.user.service.UserService;
import com.linyi.user.service.adapter.BadgeRespAdapter;
import com.linyi.user.service.adapter.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    UserBackpackDao userBackpackDao;
    @Autowired
    ItemConfigDao itemConfigDao;
    @Override
    public void register(User user) {
        userDao.save(user);
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
//        查询uid对应的用户信息
        User user = userDao.getByUid(uid);
//        查询用户改名卡数量
        Integer renameCardNum = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
//        构建返回对象
        return UserAdapter.buildUserInfoResp(user,renameCardNum);
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
//        查询当前所有徽章
        List<ItemConfig> badges = itemConfigDao.getByType(ItemTypeEnum.BADGE.getType());
//        查询用户拥有的徽章
        List<UserBackpack> userBadges = userBackpackDao.getByItemIds(uid, badges.stream().map(ItemConfig::getId).collect(Collectors.toList()));
//        查询用户佩戴的徽章
        User byUid = userDao.getByUid(uid);
//        构建返回对象
        return BadgeRespAdapter.bulidBadgeResp(badges,userBadges,byUid);

    }
}
