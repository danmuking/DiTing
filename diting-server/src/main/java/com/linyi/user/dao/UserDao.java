package com.linyi.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.common.domain.enums.NormalOrNoEnum;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.ChatActiveStatusEnum;
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

    public Integer getOnlineCount(List<Long> memberUidList) {
        return lambdaQuery()
                .in(User::getId, memberUidList)
                .eq(User::getActiveStatus, ChatActiveStatusEnum.ONLINE.getStatus())
                .count();
    }

    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq request, ChatActiveStatusEnum chatActiveStatusEnum) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
//            在线或是离线
            wrapper.eq(User::getActiveStatus, chatActiveStatusEnum.getStatus());
            wrapper.in(CollectionUtils.isNotEmpty(memberUidList), User::getId, memberUidList);//普通群对uid列表做限制
        }, User::getLastOptTime);
    }

    public List<User> getMemberList() {
        return lambdaQuery()
                .eq(User::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .orderByDesc(User::getLastOptTime)//最近活跃的1000个人，可以用lastOptTime字段，但是该字段没索引，updateTime可平替
                .last("limit 1000")//毕竟是大群聊，人数需要做个限制
                .select(User::getId, User::getName, User::getAvatar)
                .list();
    }
}
