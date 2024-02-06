package com.linyi.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.linyi.common.constant.RedisKey;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
import com.linyi.common.utils.RedisUtils;
import com.linyi.user.dao.BlackDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.dao.UserRoleDao;
import com.linyi.user.domain.entity.Black;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.entity.UserRole;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.linyi.common.constant.J2CacheConstant.REGION;

/**
 * @program: DiTing
 * @description: 用户缓存
 * @author: lin
 * @create: 2024-02-04 23:12
 **/
@Component
public class UserCache {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private CacheChannel cacheChannel;
    @Autowired
    private UserSummaryCache userSummaryCache;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private BlackDao blackDao;
    /**
     * @param :
     * @return Long
     * @description 获取在线用户数量
     * @date 2024/2/6 20:44
     */
    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(onlineKey);
    }

    /**
     * @param uid:
     * @return void
     * @description 移除用户
     * @date 2024/2/6 20:45
     */
    public void remove(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //移除上线表
        RedisUtils.zRemove(onlineKey, uid);
    }

    /**
     * @param uid:
     * @param optTime:
     * @return void
     * @description 用户上线
     * @date 2024/2/6 20:46
     */
    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }

    /**
     * @param :
     * @return List<Long>
     * @description 获取在线用户列表
     * @date 2024/2/6 20:48
     */
    public List<Long> getOnlineUidList() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        Set<String> strings = RedisUtils.zAll(onlineKey);
        return strings.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    /**
     * @param uid:
     * @return boolean
     * @description 用户是否在线
     * @date 2024/2/6 20:49
     */
    public boolean isOnline(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zIsMember(onlineKey, uid);
    }

    /**
     * @param uid:
     * @param optTime:
     * @return void
     * @description 用户下线
     * @date 2024/2/6 20:50
     */
    public void offline(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除上线线表
        RedisUtils.zRemove(onlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(offlineKey, uid, optTime.getTime());
    }

    /**
     * @param :
     * @return Long
     * @description 获取离线用户数量
     * @date 2024/2/6 20:44
     */
    public Long getOfflineNum() {
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        return RedisUtils.zCard(offlineKey);
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOnlineCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.ONLINE_UID_ZET), Long::parseLong);
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOfflineCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.OFFLINE_UID_ZET), Long::parseLong);
    }
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
        return userInfoCache.getBatch(new ArrayList<>(uids));
    }

    /**
     * @param uidList:
     * @return List<Long>
     * @description 获取用户最后修改时间
     * @date 2024/2/6 21:18
     */
    public List<Long> getUserModifyTime(List<Long> uidList) {
        ArrayList<CacheObject> cacheObjects = new ArrayList<>();
        for(Long uid : uidList){
            cacheObjects.add(cacheChannel.get(REGION, RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)));
        }
        return cacheObjects.stream().map(cacheObject -> (Long) cacheObject.getValue()).collect(Collectors.toList());
    }

    /**
     * @param uid:
     * @return void
     * @description 刷新用户修改时间
     * @date 2024/2/6 21:19
     */
    public void refreshUserModifyTime(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid);
        cacheChannel.set(REGION, key, new Date().getTime());
    }

    /**
     * @param uid:
     * @return void
     * @description 用户信息改变事件
     * @date 2024/2/6 21:21
     */
    public void userInfoChange(Long uid) {
        delUserInfo(uid);
        //删除UserSummaryCache，前端下次懒加载的时候可以获取到最新的数据
        userSummaryCache.delete(uid);
        refreshUserModifyTime(uid);
    }

    /**
     * @param uid:
     * @return void
     * @description 删除用户缓存
     * @date 2024/2/6 21:23
     */
    public void delUserInfo(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
        cacheChannel.evict(REGION, key);
    }

//    TODO:细粒度缓存
    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    @Cacheable(cacheNames = "user", key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
