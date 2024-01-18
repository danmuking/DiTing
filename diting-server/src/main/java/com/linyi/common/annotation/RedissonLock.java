package com.linyi.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @package: com.linyi.common.annotation
 * @className: RedissonLock
 * @author: Lin
 * @description: 分布式锁注解
 * @date: 2024/1/17 22:25
 * @version: 1.0
 */
// 将注解保留到运行时
@Retention(RetentionPolicy.RUNTIME)
// 注解作用在方法上
@Target(ElementType.METHOD)
public @interface RedissonLock {
    /**
     * key的前缀,默认取方法全限定名，除非我们在不同方法上对同一个资源做分布式锁，就自己指定
     *
     * @return key的前缀
     */
    String prefixKey() default "";

    /**
     * springEl 表达式
     *
     * @return 表达式
     */
    String key();

    /**
     * 等待锁的时间，默认-1，不等待直接失败,redisson默认也是-1
     *
     * @return 单位秒
     */
    int waitTime() default -1;

    /**
     * 等待锁的时间单位，默认毫秒
     *
     * @return 单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
