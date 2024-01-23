package com.linyi;

import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

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
        User byId = userDao.getById(11021);
        System.out.println(byId);
    }

    @Test
    public void testAsync() throws InterruptedException {
        System.out.println("testAsync");
        Thread.sleep(2000);
    }
    @Test
    public void generateToken(){
        String login = loginService.login(11022L);
        System.out.println(login);
    }
}
