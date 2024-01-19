package com.linyi.common.event.listener;

import com.linyi.common.event.UserOnlineEvent;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.IpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @program: DiTing
 * @description: 用户上线时间监听器
 * @author: lin
 * @create: 2024-01-19 14:42
 **/
@Component
public class UserOnlineEventListener {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IpService ipService;
    @EventListener(classes = UserOnlineEvent.class)
    public void updateIpInfo(UserOnlineEvent userOnlineEvent) {
//        获取用户信息
        User user = userOnlineEvent.getUser();
//        将用户ip信息保存到数据库
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        userDao.updateById(update);
        //更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
    }
}
