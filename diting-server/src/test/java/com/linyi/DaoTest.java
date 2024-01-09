package com.linyi;

import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class DaoTest {
    @Autowired
    private UserDao userDao;

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
}
