package com.linyi.user.service.impl;

import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Override
    public void register(User user) {
        userDao.save(user);
    }
}
