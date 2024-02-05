package com.linyi.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.linyi.common.constant.RedisKey;
import com.linyi.common.utils.RedisUtils;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.velocity.runtime.directive.Foreach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description: 用户缓存
 * @author: lin
 * @create: 2024-02-04 23:12
 **/
@Component
public class UserCache {
    private static final String BASE_REGIN = "user_%d";
    @Autowired
    private UserDao userDao;

    @Autowired
    private CacheChannel cacheChannel;

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid) {
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }
    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        Map<String, Object> collect = uids.stream().map(a -> {
            String regin = String.format(BASE_REGIN, a);
            String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, a);
            return new Pair<>(key, cacheChannel.get(regin, key));
        }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        Map<Long, User> map = collect.values().stream().filter(Objects::isNull).map(a -> (User) a).collect(Collectors.toMap(User::getId, Function.identity()));
        //发现差集——还需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !map.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            //批量load
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            needLoadUserList.forEach(a->{
                String regin = String.format(BASE_REGIN, a.getId());
                String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, a.getId());
                cacheChannel.set(regin, key, a);
            });
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return map;
    }

    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }

    public User getByUid(Long uid) {
        return getUserInfo(uid);
    }

    public void delByUid(Long uid) {
        String regin = String.format(BASE_REGIN, uid);
        String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
        cacheChannel.evict(regin, key);
    }

    public void delByRegion(Long uid) {
        String regin = String.format(BASE_REGIN, uid);
        cacheChannel.clear(regin);
    }
}
