package com.linyi.user.dao;

import cn.hutool.core.lang.Opt;
import com.linyi.user.domain.entity.UserEmoji;
import com.linyi.user.mapper.UserEmojiMapper;
import com.linyi.user.service.IUserEmojiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-02-08
 */
@Service
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji> {

    public int countByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).count();
    }

    public List<UserEmoji> listByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).list();
    }
}
