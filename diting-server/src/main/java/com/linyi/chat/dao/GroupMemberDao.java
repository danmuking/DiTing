package com.linyi.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.domain.enums.GroupRoleEnum;
import com.linyi.chat.mapper.GroupMemberMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    public GroupMember getMember(Long id, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getUid, uid)
                .one();
    }

    public List<Long> getMemberUidList(Long id) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .select(GroupMember::getUid)
                .list()
                .stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());
    }

    public Map<Long, Integer> getMemberMapRole(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .in(GroupMember::getRole, GroupRoleEnum.ADMIN_LIST)
                .select(GroupMember::getUid, GroupMember::getRole)
                .list();
        return list.stream().collect(Collectors.toMap(GroupMember::getUid, GroupMember::getRole));
    }

    public Boolean isLord(Long groupId, Long removedUid) {
        GroupMember groupMember = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, removedUid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    public boolean isManager(Long groupId, Long removedUid) {
        GroupMember groupMember = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, removedUid)
                .in(GroupMember::getRole, GroupRoleEnum.MANAGER)
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * @param roomId:
     * @param longs:
     * @return Boolean
     * @description 用户是否在群中
     * @date 2024/1/29 19:01
     */
    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = getMemberUidList(roomId);
        return memberUidList.containsAll(uidList);
    }

    public Boolean removeByGroupId(Long groupId, List uidList) {
        if (CollectionUtil.isNotEmpty(uidList)) {
            LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                    .lambda()
                    .eq(GroupMember::getGroupId, groupId)
                    .in(GroupMember::getUid, uidList);
            return this.remove(wrapper);
        }
        return false;
    }

    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .list();
    }

    public List<Long> getMemberBatch(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public List<Long> getManageUidList(Long id) {
        return this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getRole, GroupRoleEnum.MANAGER.getType())
                .list()
                .stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());
    }

    public void addAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.MANAGER.getType());
        this.update(wrapper);
    }
}
