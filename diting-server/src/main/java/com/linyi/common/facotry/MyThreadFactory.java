package com.linyi.common.facotry;

import com.linyi.common.handler.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

/**
 * @package: com.linyi.common.facotry
 * @className: MyThreadFactory
 * @author: Lin
 * @description: 线程工厂，用于进行异常捕获
 * @date: 2024/3/1 23:28
 * @version: 1.0
 */
@Slf4j
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {
    private final ThreadFactory factory;
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = factory.newThread(r);
        thread.setUncaughtExceptionHandler(GlobalUncaughtExceptionHandler.getInstance());
        return thread;
    }
}
