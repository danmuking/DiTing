package com.linyi.common.event;

import com.linyi.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @program: DiTing
 * @description: 用户上线事件
 * @author: lin
 * @create: 2024-01-19 14:39
 **/
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private final User user;

    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
