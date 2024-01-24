package com.linyi.user.dao;

import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
import com.linyi.user.domain.entity.UserFriend;
import com.linyi.user.domain.vo.request.friend.FriendCheckReq;
import com.linyi.user.mapper.UserFriendMapper;
import com.linyi.user.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    public UserFriend getByFriend(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .one();
    }

    public List<UserFriend> getUserFriend(Long uid, Long friendUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, friendUid)
                .or()
                .eq(UserFriend::getFriendUid, uid)
                .eq(UserFriend::getUid, friendUid)
                .select(UserFriend::getId)
                .list();
    }

    public CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq,
                wrapper -> wrapper.eq(UserFriend::getUid, uid), UserFriend::getId);
    }

    public List<UserFriend> getByFriends(Long uid, List<Long> uidList) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .in(UserFriend::getFriendUid, uidList)
                .list();
    }
}
