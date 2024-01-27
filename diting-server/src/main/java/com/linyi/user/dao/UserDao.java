package com.linyi.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.user.domain.entity.User;
import com.linyi.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-09
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    /**
     * @param openId:
     * @return User
     * @description 根据openid查询用户
     * @date 2024/1/11 18:47
     */
    public User getByOpenId(String openId){
        LambdaQueryWrapper<User> eq = new QueryWrapper<User>().lambda().eq(User::getOpenId, openId);
        return getOne(eq);
    }

    /**
     * @param uid:
     * @return User
     * @description 根据uid查询用户
     * @date 2024/1/16 20:04
     */
    public User getByUid(Long uid){
        return getById(uid);
    }

    public void wearingBadge(Long uid, @NotNull Long badgeId) {
        User update = new User();
        update.setId(uid);
        update.setItemId(badgeId);
        updateById(update);
    }

    public User getByName(String name) {
        return lambdaQuery()
                .eq(User::getName, name)
                .one();
    }

    public void modifyName(Long uid, String name) {
        User user = new User();
        user.setId(uid);
        user.setName(name);
        updateById(user);
    }

    public List<User> getFriendList(List<Long> friendUids) {
        return lambdaQuery()
                .in(User::getId, friendUids)
                .select(User::getId, User::getActiveStatus, User::getName, User::getAvatar)
                .list();
    }

    public List<User> getBatchByIds(List<Long> collect) {
        return lambdaQuery()
                .in(User::getId, collect)
                .list();
    }
}
