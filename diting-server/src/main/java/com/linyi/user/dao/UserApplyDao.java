package com.linyi.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.user.domain.entity.UserApply;
import com.linyi.user.domain.enums.ApplyReadStatusEnum;
import com.linyi.user.domain.enums.ApplyStatusEnum;
import com.linyi.user.domain.enums.ApplyTypeEnum;
import com.linyi.user.mapper.UserApplyMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetUid)
                .one();
    }

    public void agree(Long applyId) {
        lambdaUpdate()
                .set(UserApply::getStatus, ApplyStatusEnum.AGREE.getCode())
                .eq(UserApply::getId, applyId)
                .update();
    }

    public IPage<UserApply> friendApplyPage(Long uid, Page page) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getCode())
                .orderByDesc(UserApply::getCreateTime)
                .page(page);
    }

    public void readApples(Long uid, List<Long> applyIds) {
        lambdaUpdate()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getCode())
                .in(UserApply::getId, applyIds)
                .set(UserApply::getReadStatus, ApplyReadStatusEnum.READ.getCode())
                .update();
    }

    public Integer getUnReadCount(Long uid) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getCode())
                .count();
    }
}
