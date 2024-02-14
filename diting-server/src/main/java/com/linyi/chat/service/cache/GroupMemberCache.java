package com.linyi.chat.service.cache;

import com.linyi.chat.dao.GroupMemberDao;
import com.linyi.chat.dao.RoomGroupDao;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.domain.entity.RoomGroup;
import com.linyi.common.service.cache.AbstractJ2Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-14 15:34
 **/
@Component
public class GroupMemberCache extends AbstractJ2Cache<Long, GroupMember> {
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Override
    protected String getKey(Long req) {
        return null;
    }

    @Override
    protected Map<Long, GroupMember> load(List<Long> req) {
        return null;
    }

    @Cacheable(value = "groupMember", key = "#roomGroupId + ':' + #uid")
    public GroupMember getMember(Long roomGroupId, Long uid) {
        return groupMemberDao.getMember(roomGroupId, uid);
    }

    @Cacheable(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }
}
