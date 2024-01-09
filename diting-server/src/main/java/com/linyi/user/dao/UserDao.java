package com.linyi.user.dao;

import com.linyi.user.domain.entity.User;
import com.linyi.user.mapper.UserMapper;
import com.linyi.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-09
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> implements IUserService {

}
