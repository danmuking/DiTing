package com.linyi.user.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.linyi.common.handler.GlobalUncaughtExceptionHandler;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.dto.IpResult;
import com.linyi.user.domain.entity.IpDetail;
import com.linyi.user.domain.entity.IpInfo;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.IpService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-18 17:55
 **/
@Slf4j
@Service
public class IpServiceImpl implements IpService, DisposableBean {
//    只有一个线程的线程池
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false,
                    GlobalUncaughtExceptionHandler.getInstance()));
    @Autowired
    UserDao userDao;
    @Override
    public void refreshIpDetailAsync(Long uid) {
        EXECUTOR.execute(() ->{
            //        查询用户信息
            User byUid = userDao.getByUid(uid);
            IpInfo ipInfo = byUid.getIpInfo();
//        检查是否有ip信息
            if(Objects.isNull(ipInfo)){
                return;
            }
//        检查是否有需要更新的ip信息
            String ip = ipInfo.needRefreshIp();
            if(StringUtil.isBlank(ip)){
                return;
            }
//        获取ip详情
            IpDetail ipDetail = TryGetIpDetailOrNullThreeTimes(ip);
//        更新ip详情
            if(Objects.nonNull(ipDetail)){
//            更新ip详情
                ipInfo.refreshIpDetail(ipDetail);
                byUid.setIpInfo(ipInfo);
//            刷回数据库
                userDao.updateById(byUid);
            }
        });
    }

    /**
     * @param ip:
     * @return IpDetail 失败返回null
     * @description 尝试获取3次ip详情
     * @date 2024/1/18 18:28
     */
    private static IpDetail TryGetIpDetailOrNullThreeTimes(String ip) {
//        尝试3次
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
//            不为空，返回
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param ip:
     * @return IpDetail 失败返回null
     * @description 获取ip详情
     * @date 2024/1/18 18:27
     */
    public static IpDetail getIpDetailOrNull(String ip) {
//        从淘宝获取ip详情
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
//            装配
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {
            }, false);
//            判断是否成功
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    //测试耗时结果 100次查询总耗时约100s，平均一次成功查询需要1s,可以接受
    //第99次成功,目前耗时：99545ms
    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = TryGetIpDetailOrNullThreeTimes("113.90.36.126");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date();
                    System.out.println(String.format("第%d次成功,目前耗时：%dms", finalI, (date.getTime() - begin.getTime())));
                }
            });
        }
    }
    @Override
    public void destroy() throws InterruptedException {
        EXECUTOR.shutdown();
        if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {//最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }
        }
    }
}
