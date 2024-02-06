package com.linyi.common.event;

import com.linyi.user.domain.entity.User;
import com.linyi.user.service.impl.UserServiceImpl;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-06 22:00
 **/
@Getter
public class UserBlackEvent extends ApplicationEvent {
    private final User user;

    public UserBlackEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
