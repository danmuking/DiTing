package com.linyi.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.common.domain.enums.YesOrNoEnum;
import com.linyi.user.domain.entity.UserBackpack;
import com.linyi.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-16
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    public Integer getCountByValidItemId(Long uid, Long id) {
        return lambdaQuery()
                .eq(UserBackpack::getUid,uid)
                .eq(UserBackpack::getItemId,id)
//                状态为未使用
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> ids) {
        List<UserBackpack> list = lambdaQuery().eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, ids)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .list();
        return list;
    }

    public UserBackpack getByItemId(Long uid, Long itemId) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .last("limit 1")
                .one();
    }

    public UserBackpack getFirstValidItem(Long uid, Long id) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, id)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .last("limit 1")
                .one();
    }

    public boolean invalidItem(Long id) {
        UserBackpack update = new UserBackpack();
        update.setId(id);
        update.setStatus(YesOrNoEnum.YES.getStatus());
        return updateById(update);
    }

    public UserBackpack getByIdp(String idempotent) {
        return lambdaQuery()
                .eq(UserBackpack::getIdempotent, idempotent)
                .one();
    }
}
