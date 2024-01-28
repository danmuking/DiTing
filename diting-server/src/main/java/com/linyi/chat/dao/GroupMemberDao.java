package com.linyi.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.mapper.GroupMemberMapper;
import com.linyi.chat.service.IGroupMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
