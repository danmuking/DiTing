package com.linyi.user.service.impl;

import com.linyi.common.constant.RedisKey;
import com.linyi.common.utils.JwtUtils;
import com.linyi.common.utils.RedisUtils;
import com.linyi.user.service.LoginService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    public static final long USER_TOKEN_EXPIRE = 3;
    private static final long USER_TOKEN_RENEW = 1;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean verify(String token) {
        //        获取uid
        Long uid = jwtUtils.getUidOrNull(token);
        if(uid == null){
            return false;
        }
//        获取redis中的token
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String realToken = RedisUtils.getStr(key);
//        判断token和当前token是否一致
        return Objects.equals(token, realToken);
    }

    @Async
    @Override
    public void renewalTokenIfNecessary(String token) {
//        判断token是否有效
        if(!verify(token)){
            return;
        }
//        获取uid
        Long uid = jwtUtils.getUidOrNull(token);
        if(uid == null){
            return;
        }
//        获取redis中的token过期时间
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        long expireDays = RedisUtils.getExpire(key, TimeUnit.DAYS);
//        如果过期天数小于续期天数，续期
        if(expireDays < USER_TOKEN_RENEW){
            RedisUtils.expire(key, USER_TOKEN_EXPIRE, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
//        如果用户已登录，直接从redis返回token
        String token = RedisUtils.getStr(key);
        if(StringUtils.isNotBlank(token)){
            return token;
        }
//        获取token
        token = jwtUtils.createToken(uid);
//        将token存入redis
        RedisUtils.set(key, token, USER_TOKEN_EXPIRE, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
//        返回uid
        return verify(token) ? jwtUtils.getUidOrNull(token) : null;
    }

}
