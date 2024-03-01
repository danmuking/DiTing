package com.linyi;

import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
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
@Slf4j
public class DaoTest {
    @Autowired
    private UserDao userDao;

    @Autowired
    LoginService loginService;
    @Autowired
    private CacheChannel cacheChannel;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

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
        String login = loginService.login(1L);
        System.out.println(login);
    }

    @Test
    public void testCache(){
        CacheChannel cache = cacheChannel;

        //缓存操作
        cache.set("default", "1", "Hello J2Cache");
        System.out.println(cache.get("default", "1"));
        cache.evict("default", "1");
        System.out.println(cache.get("default", "1"));

        cache.close();
    }
    @Test
    public void sendMQ() {
        Message<String> build = MessageBuilder.withPayload("123").build();
        rocketMQTemplate.send("test-topic", build);
    }
    @Test
    public void threadTest(){
        Thread thread = new Thread(() -> {
            log.info("111");
            throw new RuntimeException("运行时异常了");
        });
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler =(t,e)->{
            log.error("Exception in thread ",e);
        };
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        thread.start();
    }
}
