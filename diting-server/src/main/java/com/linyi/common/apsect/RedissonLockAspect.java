package com.linyi.common.apsect;

import cn.hutool.core.util.StrUtil;
import com.linyi.common.annotation.RedissonLock;
import com.linyi.common.service.LockService;
import com.linyi.common.utils.SpElUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @package: com.linyi.common.apsect
 * @className: RedissonLockAspect
 * @author: Lin
 * @description: 分布式锁切面
 * @date: 2024/1/17 22:30
 * @version: 1.0
 */
@Slf4j
@Aspect
@Component
@Order(0)//确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {
    @Autowired
    private LockService lockService;

    @Around("@annotation(com.linyi.common.annotation.RedissonLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
//        通过反射获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        获取方法上的注解
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        //默认方法限定名+注解排名（可能多个）
//        是否指定了前缀，如果没有指定，就用默认的
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
//        解析springEl表达式作为key
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        return lockService.executeWithLockThrows(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
