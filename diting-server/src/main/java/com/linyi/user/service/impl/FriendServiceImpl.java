package com.linyi.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.linyi.common.annotation.RedissonLock;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.request.PageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.domain.vo.response.PageBaseResp;
import com.linyi.common.event.UserApplyEvent;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.UserApplyDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.dao.UserFriendDao;
import com.linyi.user.domain.entity.RoomFriend;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserApply;
import com.linyi.user.domain.entity.UserFriend;
import com.linyi.user.domain.vo.request.friend.FriendApplyReq;
import com.linyi.user.domain.vo.request.friend.FriendCheckReq;
import com.linyi.user.domain.vo.request.friend.FriendCheckResp;
import com.linyi.user.domain.vo.response.friend.FriendApplyResp;
import com.linyi.user.domain.vo.response.friend.FriendApproveReq;
import com.linyi.user.domain.vo.response.friend.FriendResp;
import com.linyi.user.domain.vo.response.friend.FriendUnreadResp;
import com.linyi.user.service.FriendService;
import com.linyi.user.service.RoomService;
import com.linyi.user.service.adapter.FriendAdapter;
import jdk.nashorn.internal.objects.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.linyi.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;

/**
 * @package: com.linyi.user.service.impl
 * @className: FriendServiceImpl
 * @author: Lin
 * @description: 好友相关接口实现
 * @date: 2024/1/22 22:01
 * @version: 1.0
 */
@Service
@Slf4j
public class FriendServiceImpl implements FriendService {
    @Autowired
    UserFriendDao userFriendDao;
    @Autowired
    UserApplyDao userApplyDao;
    @Autowired
    UserDao userDao;
    @Autowired
    @Lazy
    private FriendServiceImpl friendService;
    @Autowired
    RoomService roomService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    @RedissonLock(key = "#uid")
    public void apply(Long uid, FriendApplyReq request) {
//        判断是否已经是好友
        UserFriend friend = userFriendDao.getByFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友了");
//        是否已经发送过好友请求
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid());
        if (Objects.nonNull(selfApproving)) {
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, request.getTargetUid());
            return;
        }
//        对方是否给你发送过好友申请,如果发送过，直接成为好友
        UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid);
        if(Objects.nonNull(friendApproving)) {
            friendService.applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }
//        如果没有发送过好友申请，创建好友申请记录
        UserApply insert = FriendAdapter.buildFriendApply(uid, request);
        userApplyDao.save(insert);
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, insert));
    }


    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid+\"_\"+#friendApproveReq.getApplyId()")
    public void applyApprove(Long uid, FriendApproveReq friendApproveReq) {
//        验证申请记录是否存在
        UserApply userApply = userApplyDao.getById(friendApproveReq.getApplyId());
//        记录不存在报错
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
//        申请对象不是自己报错
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录");
//        申请状态不是等待同意报错
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "已同意好友申请");
//        同意申请，修改申请状态
        userApplyDao.agree(friendApproveReq.getApplyId());
//        好友关系入库
        createFriend(uid, userApply.getUid());
//        创建聊天房间
        RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, userApply.getUid()));
//        TODO: 发送同意消息
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFriend(Long uid, Long targetUid) {
//        判断uid是否存在
        User byUid = userDao.getByUid(uid);
        AssertUtil.isNotEmpty(byUid, "用户不存在");
//        判断是否存在好友关系
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, targetUid);
        AssertUtil.isNotEmpty(userFriends, "不存在好友关系");
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
//        删除好友关系
//        TODO:逻辑删除
        userFriendDao.removeByIds(friendRecordIds);
//        禁用房间
        roomService.disableFriendRoom(Arrays.asList(uid, targetUid));
    }

    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
//        分页查询好友申请
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid, request.plusPage());
//        如果没有好友申请，返回空页
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())) {
            return PageBaseResp.empty();
        }
//        将这些申请列表设为已读
        readApples(uid, userApplyIPage);
        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
//        分页查询好友列表
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
//        没有好友，返回空页
        if (CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }
//        查询好友信息
        List<Long> friendUids = friendPage.getList()
                .stream().map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> userList = userDao.getFriendList(friendUids);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), userList));
    }

    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
//        批量获取好友关系
        List<UserFriend> friendList = userFriendDao.getByFriends(uid,request.getUidList());
//        判断是否存在好友关系
//        构建好友集合
        Set<Long> friends = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
//        构建返回结果
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid ->{
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friends.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());

        return new FriendCheckResp(friendCheckList);
    }

    private void readApples(Long uid, IPage<UserApply> userApplyIPage) {
        List<Long> applyIds = userApplyIPage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApples(uid, applyIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend1, userFriend2));
    }
}
