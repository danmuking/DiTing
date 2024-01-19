package com.linyi.common.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 全局未捕获线程处理
 * @date 2024/1/18 18:32
 */
@Slf4j
public class GlobalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final GlobalUncaughtExceptionHandler instance = new GlobalUncaughtExceptionHandler();

    private GlobalUncaughtExceptionHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread {} ", t.getName(), e);
    }

    public static GlobalUncaughtExceptionHandler getInstance() {
        return instance;
    }

}