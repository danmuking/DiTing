package com.linyi.user.service.cache;

import com.linyi.common.constant.RedisKey;
import com.linyi.common.service.cache.AbstractJ2Cache;
import com.linyi.common.service.cache.AbstractRedisStringCache;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description: 用户基本信息缓存
 * @author: lin
 * @create: 2024-01-30 19:52
 **/
@Component
public class UserInfoCache extends AbstractJ2Cache<Long, User> {
    @Autowired
    private UserDao userDao;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
    }

    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        List<User> needLoadUserList = userDao.listByIds(uidList);
        return needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
