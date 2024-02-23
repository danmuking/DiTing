package com.linyi.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.linyi.common.event.UserBlackEvent;
import com.linyi.common.event.UserRegisterEvent;
import com.linyi.common.utils.AssertUtil;
import com.linyi.util.RequestHolder;
import com.linyi.user.dao.BlackDao;
import com.linyi.user.dao.ItemConfigDao;
import com.linyi.user.dao.UserBackpackDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.dto.ItemInfoDTO;
import com.linyi.user.domain.dto.SummeryInfoDTO;
import com.linyi.user.domain.entity.Black;
import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserBackpack;
import com.linyi.user.domain.enums.*;
import com.linyi.user.domain.vo.request.friend.ItemInfoReq;
import com.linyi.user.domain.vo.request.user.BlackReq;
import com.linyi.user.domain.vo.request.user.ModifyNameReq;
import com.linyi.user.domain.vo.request.user.SummeryInfoReq;
import com.linyi.user.domain.vo.request.user.WearingBadgeReq;
import com.linyi.user.domain.vo.response.user.BadgeResp;
import com.linyi.user.domain.vo.response.user.UserInfoResp;
import com.linyi.user.service.IRoleService;
import com.linyi.user.service.UserService;
import com.linyi.user.service.adapter.BadgeRespAdapter;
import com.linyi.user.service.adapter.UserAdapter;
import com.linyi.user.service.cache.ItemCache;
import com.linyi.user.service.cache.UserBackpackCache;
import com.linyi.user.service.cache.UserCache;
import com.linyi.user.service.cache.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    UserBackpackDao userBackpackDao;
    @Autowired
    ItemConfigDao itemConfigDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private UserCache userCache;
    @Autowired
    private UserSummaryCache userSummaryCache;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private UserBackpackCache userBackpackCache;
    @Autowired
    private BlackDao blackDao;
    @Override
    public void register(User user) {
        userDao.save(user);
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
//        查询uid对应的用户信息
        User user = userCache.getUserInfo(uid);
//        查询用户改名卡数量
        Integer renameCardNum = userBackpackCache.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
//        构建返回对象
        return UserAdapter.buildUserInfoResp(user,renameCardNum);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user",key = "'userInfo'+#uid")
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
            userBackpackCache.InvalidCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
            userCache.userInfoChange(uid);
            userDao.modifyName(uid, req.getName());
        }
    }

    @Cacheable(value = "user",key = "'badges'+#uid")
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

    @CacheEvict(value = "user",key = "'badges'+#uid")
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
//            删除用户缓存
            userCache.userInfoChange(uid);
            userDao.wearingBadge(byUid.getId(),req.getBadgeId());
        }
    }

    /**
     * @param req:
     * @return void
     * @description 拉黑用户，包括用户名和ip
     * @date 2024/2/6 22:09
     */
    @Override
    @Transactional
    public void black(BlackReq req) {
        Long uid = RequestHolder.get().getUid();
//        判断当前操作用户是否有权限
//        TODO: 多种权限判断
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "没有权限");
        User update = User.builder().id(req.getUid()).status(UserStatusEnum.BLACK.getStatus()).build();
        userDao.updateById(update);
//        拉黑用户
        Black user = new Black();
        user.setTarget(uid.toString());
        user.setType(BlackTypeEnum.UID.getType());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(byId.getIpInfo().getCreateIp());
        blackIp(byId.getIpInfo().getUpdateIp());
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    /**
     * @param ip:
     * @return void
     * @description 拉黑ip
     * @date 2024/2/6 22:10
     */
    private void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }

    @Override
    public List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req) {
//        需要前端同步的uid
        List<Long> uidList = getNeedSyncUidList(req.getReqList());
//        加载用户信息
        Map<Long, SummeryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getReqList()
                .stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummeryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * @param reqList:
     * @return List<Long>
     * @description 获取需要刷新的用户id
     * @date 2024/2/6 22:11
     */
    private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > infoReq.getLastModifyTime())) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }


    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {
        //简单做，更新时间可判断被修改
        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.getById(a.getItemId());
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return ItemInfoDTO.skip(a.getItemId());
            }
            ItemInfoDTO dto = new ItemInfoDTO();
            dto.setItemId(itemConfig.getId());
            dto.setImg(itemConfig.getImg());
            dto.setDescribe(itemConfig.getDescribe());
            return dto;
        }).collect(Collectors.toList());
    }


}
