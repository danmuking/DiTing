package com.linyi.common.event;

import com.linyi.user.domain.entity.UserApply;
import com.linyi.user.service.impl.FriendServiceImpl;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-07 17:20
 **/
@Getter
public class UserApplyEvent extends ApplicationEvent {
    private UserApply userApply;

    public UserApplyEvent(Object source, UserApply userApply) {
        super(source);
        this.userApply = userApply;
    }

}
