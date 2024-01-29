package com.linyi.chat.dao;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.domain.enums.GroupRoleAPPEnum;
import com.linyi.chat.domain.enums.GroupRoleEnum;
import com.linyi.chat.mapper.GroupMemberMapper;
import com.linyi.chat.service.IGroupMemberService;
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
}
