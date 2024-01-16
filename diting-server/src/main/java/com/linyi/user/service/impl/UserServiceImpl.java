package com.linyi.user.service.impl;

import com.linyi.user.dao.UserBackpackDao;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.ItemEnum;
import com.linyi.user.domain.vo.response.user.UserInfoResp;
import com.linyi.user.service.UserService;
import com.linyi.user.service.adapter.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    UserBackpackDao userBackpackDao;
    @Override
    public void register(User user) {
        userDao.save(user);
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
//        查询uid对应的用户信息
        User user = userDao.getByUid(uid);
//        查询用户改名卡数量
        Integer renameCardNum = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
//        构建返回对象
        return UserAdapter.buildUserInfoResp(user,renameCardNum);
    }
}
