package com.linyi.user.service;

import com.linyi.common.domain.enums.IdempotentEnum;
import com.linyi.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author lin
 * @since 2024-01-16
 */
public interface IUserBackpackService{

    /**
     * @param uid: 用户id
     * @param itemId: 物品id
     * @param idempotentEnum: 幂等类型
     * @param businessId: 上层业务发送的唯一标识
     * @return void
     * @description 给用户发放一个物品
     * @date 2024/1/17 22:00
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
