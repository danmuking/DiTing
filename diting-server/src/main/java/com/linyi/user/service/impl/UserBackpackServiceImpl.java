package com.linyi.user.service.impl;

import com.linyi.common.annotation.RedissonLock;
import com.linyi.common.domain.enums.IdempotentEnum;
import com.linyi.common.domain.enums.YesOrNoEnum;
import com.linyi.common.exception.CommonErrorEnum;
import com.linyi.user.dao.ItemConfigDao;
import com.linyi.user.dao.UserBackpackDao;
import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.domain.entity.UserBackpack;
import com.linyi.user.domain.enums.ItemEnum;
import com.linyi.user.domain.enums.ItemTypeEnum;
import com.linyi.user.service.IUserBackpackService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @package: com.linyi.user.service.impl
 * @className: UserBackpackServiceImpl
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/17 22:07
 * @version: 1.0
 */
@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
//        组装幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
//        发放物品,防止类内调用注解失效
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    @Async
    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
//        检查幂等号是否已经存在
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotent);
//        已存在，直接返回
        if(Objects.nonNull(userBackpack)){
            return;
        }
//        如果是徽章，额外判断徽章是否已存在
        ItemConfig byId = itemConfigDao.getById(itemId);
//        判断是否为徽章
        if(Objects.nonNull(byId) && byId.getType() == ItemTypeEnum.BADGE.getType()){
//            如果已存在，直接返回
            Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
            if(countByValidItemId>0)
                return;
        }
//        发放物品
        UserBackpack insert = UserBackpack
                .builder()
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .uid(uid)
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
