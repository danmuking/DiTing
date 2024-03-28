package com.linyi.common.event;

import com.linyi.user.domain.entity.User;
import com.linyi.user.service.impl.WebSocketServiceImpl;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-16 11:10
 **/
@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private final User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}

