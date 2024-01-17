package com.linyi;

import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;
import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @package: com.linyi
 * @className: DaoTest
 * @author: Lin
 * @description: 测试mp生成代码是否成功
 * @date: 2024/1/9 22:17
 * @version: 1.0
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@EnableAsync
public class DaoTest {
    @Autowired
    private UserDao userDao;

    @Autowired
    LoginService loginService;

    @Test
    public void test(){
        User user = new User();
        user.setId(1L);
        user.setName("lin");
        user.setOpenId("123456");
        userDao.save(user);
        user = userDao.getById(1);
        System.out.println(user);
        userDao.removeById(1);
    }

    @Test
    public void testAsync() throws InterruptedException {
        System.out.println("testAsync");
        Thread.sleep(2000);
    }
    @Test
    public void generateToken(){
        String login = loginService.login(11007L);
        System.out.println(login);
    }
}
