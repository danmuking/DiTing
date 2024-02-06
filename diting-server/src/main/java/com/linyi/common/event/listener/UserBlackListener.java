package com.linyi.common.event.listener;

import com.linyi.chat.dao.MessageDao;
import com.linyi.common.event.UserBlackEvent;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import com.linyi.user.domain.vo.response.ws.WSBlack;
import com.linyi.user.service.WebSocketService;
import com.linyi.user.service.cache.UserCache;
import com.linyi.user.service.cache.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @program: DiTing
 * @description: 用户拉黑事件监听器
 * @author: lin
 * @create: 2024-02-06 22:02
 **/
@Slf4j
@Component
public class UserBlackListener {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserCache userCache;
    @Autowired
    private UserSummaryCache userSummaryCache;

    /**
     * @param event:
     * @return void
     * @description 删除缓存
     * @date 2024/2/6 22:04
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void refreshCache(UserBlackEvent event) {
        userCache.evictBlackMap();
        userCache.remove(event.getUser().getId());
        userCache.delUserInfo(event.getUser().getId());
        userSummaryCache.delete(event.getUser().getId());
    }

    /**
     * @param event:
     * @return void
     * @description 删除消息
     * @date 2024/2/6 22:05
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void deleteMsg(UserBlackEvent event) {
        messageDao.invalidByUid(event.getUser().getId());
    }

    /**
     * @param event:
     * @return void
     * @description TODO：发送拉黑信息
     * @date 2024/2/6 22:06
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void sendPush(UserBlackEvent event) {
//        Long uid = event.getUser().getId();
//        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
//        WSBlack black = new WSBlack(uid);
//        resp.setData(black);
//        resp.setType(WSRespTypeEnum.BLACK.getType());
//        webSocketService.sendToAllOnline(resp, uid);
    }
}