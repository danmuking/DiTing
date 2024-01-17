package com.linyi.user.service.impl;

import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.ItemConfigDao;
import com.linyi.user.dao.UserBackpackDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserBackpack;
import com.linyi.user.domain.enums.ItemEnum;
import com.linyi.user.domain.enums.ItemTypeEnum;
import com.linyi.user.domain.vo.request.ModifyNameReq;
import com.linyi.user.domain.vo.request.WearingBadgeReq;
import com.linyi.user.domain.vo.response.user.BadgeResp;
import com.linyi.user.domain.vo.response.user.UserInfoResp;
import com.linyi.user.service.UserService;
import com.linyi.user.service.adapter.BadgeRespAdapter;
import com.linyi.user.service.adapter.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
//        查询用户是否拥有该徽章
        UserBackpack userBackpack = userBackpackDao.getByItemId(uid, req.getBadgeId());
//        用户没有此徽章，抛出异常
        AssertUtil.isNotEmpty(userBackpack,"用户没有此徽章");
//        判断物品是否是徽章
        ItemConfig itemConfig = itemConfigDao.getById(req.getBadgeId());
//        不是徽章，抛出异常
        AssertUtil.isTrue(Objects.equals(itemConfig.getType(), ItemTypeEnum.BADGE.getType()),"物品不是徽章");
//        查询用户是否佩戴该徽章
        User byUid = userDao.getByUid(uid);
//        用户没有佩戴该徽章，更新用户佩戴徽章
        if(!Objects.equals(byUid.getItemId(),req.getBadgeId())){
            userDao.wearingBadge(byUid.getId(),req.getBadgeId());
        }
    }

    @Override
    @Transactional
    public void modifyName(Long uid, ModifyNameReq req) {
//        判断改名卡够不够
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem,"改名次数不够了，等后续活动送改名卡哦");
//        检查名字是否重复
        User byName = userDao.getByName(req.getName());
        AssertUtil.isEmpty(byName,"名字已经被抢占了，请换一个哦~~");
//        使用改名卡
        boolean useSuccess = userBackpackDao.invalidItem(firstValidItem.getId());
//        使用成功，修改名字
        if(useSuccess) {
            userDao.modifyName(uid, req.getName());
        }
    }
}
