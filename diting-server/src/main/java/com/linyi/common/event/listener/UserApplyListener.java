package com.linyi.common.event.listener;

import com.linyi.common.event.UserApplyEvent;
import com.linyi.common.service.PushService;
import com.linyi.user.dao.UserApplyDao;
import com.linyi.user.domain.entity.UserApply;
import com.linyi.user.domain.vo.response.ws.WSFriendApply;
import com.linyi.user.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @program: DiTing
 * @description: 好友申请事件监听
 * @author: lin
 * @create: 2024-02-07 17:22
 **/
@Slf4j
@Component
public class UserApplyListener {
    @Autowired
    private UserApplyDao userApplyDao;
//    @Autowired
//    private WebSocketService webSocketService;
//
    @Autowired
    private PushService pushService;

    /**
     * @param event:
     * @return void
     * @description 通知用户好友申请
     * @date 2024/2/7 20:39
     */
    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
//        获取消息未读数
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        pushService.sendPushMsg(WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId());
    }
}
