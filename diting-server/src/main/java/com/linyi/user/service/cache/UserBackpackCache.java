package com.linyi.user.service.cache;

import com.linyi.user.dao.UserBackpackDao;
import com.linyi.user.domain.entity.UserBackpack;
import io.netty.channel.Channel;
import lombok.experimental.PackagePrivate;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.checkerframework.checker.signature.qual.PrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @program: DiTing
 * @description: 用户物品缓存
 * @author: lin
 * @create: 2024-02-05 21:18
 **/
@Component
public class UserBackpackCache {
    private static final String BASE_REGIN = "user_%d";
    private static final String PREFIX = "userBackpack";
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private CacheChannel cacheChannel;

    public UserBackpack getByItemId(Long uid, Long badgeId) {
        String regin = String.format(BASE_REGIN, uid);
        String key = PREFIX + ":" + uid + "_" + badgeId;
        if (cacheChannel.check(regin, key)!=0){
            CacheObject cacheObject = cacheChannel.get(regin, key);
            return (UserBackpack) cacheObject.getValue();
        }
        else {
            UserBackpack byItemId = userBackpackDao.getByItemId(uid, badgeId);
            cacheChannel.set(regin, key, byItemId);
            return byItemId;
        }
    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> collect) {
        String regin = String.format(BASE_REGIN, uid);
        String key = PREFIX + ":" + uid + "_" + collect;
        if (cacheChannel.check(regin, key)!=0){
            CacheObject cacheObject = cacheChannel.get(regin, key);
            return (List<UserBackpack>) cacheObject.getValue();
        }
        else {
            List<UserBackpack> byItemIds = userBackpackDao.getByItemIds(uid, collect);
            cacheChannel.set(regin, key, byItemIds);
            return byItemIds;
        }
    }

    /**
     * @param uid:
     * @param itemId:
     * @return Integer
     * @description 获取用户背包中指定物品的数量
     * @date 2024/2/5 22:28
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        String regin = String.format(BASE_REGIN, uid);
        String key = PREFIX + ":" + uid + "_" + itemId;

        Integer countByValidItemId;
        if(cacheChannel.check(regin, key)!=0){
            CacheObject cacheObject = cacheChannel.get(regin, key);
            countByValidItemId =  (Integer) cacheObject.getValue();
        }
        else {
            countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
            cacheChannel.set(regin, key, countByValidItemId);
        }
        return countByValidItemId;
    }

    public void delItemCount(Long uid, Long itemId) {
        String regin = String.format(BASE_REGIN, uid);
        String key = PREFIX + ":" + uid + "_" + itemId;
        cacheChannel.evict(regin, key);
        return;
    }
}
