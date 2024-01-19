package com.linyi.user;

import com.linyi.common.event.UserRegisterEvent;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.IUserBackpackService;
import com.linyi.user.service.IpService;
import com.linyi.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;



/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-18 17:18
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableAsync
public class ServiceTest {
    @Autowired
    IUserBackpackService userBackpackService;
    @Autowired
    UserService userService;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    IpService ipService;

    @Test
    public void testRegister(){
        User user = new User();
        user.setName("123");
        user.setAvatar("123");
        user.setOpenId("123");
        userService.register(user);
    }
    @Test
    public void testRegisterEvent() throws InterruptedException {
        User user = new User();
        user.setId(11018L);
        user.setName("123");
        user.setAvatar("123");
        user.setOpenId("123");
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
        Thread.sleep(2000);
    }
}
