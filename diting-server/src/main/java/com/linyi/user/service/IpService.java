package com.linyi.user.service;

/**
 * @program: DiTing
 * @description: ip解析服务类
 * @author: lin
 * @create: 2024-01-18 17:54
 **/
public interface IpService {
    /**
     * @param uid:
     * @return void
     * @description 异步更新用户ip详情
     * @date 2024/1/18 17:54
     */
    void refreshIpDetailAsync(Long uid);
}
