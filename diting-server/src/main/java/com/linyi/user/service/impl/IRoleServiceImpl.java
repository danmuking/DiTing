package com.linyi.user.service.impl;

import com.linyi.user.dao.UserRoleDao;
import com.linyi.user.domain.entity.UserRole;
import com.linyi.user.domain.enums.RoleEnum;
import com.linyi.user.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @package: com.linyi.user.service.impl
 * @className: IRoleServiceImpl
 * @author: Lin
 * @description: 权限服务实现类
 * @date: 2024/1/21 22:51
 * @version: 1.0
 */
@Service
public class IRoleServiceImpl implements IRoleService {
    @Autowired
    UserRoleDao userRoleDao;
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
//        查询用户角色关系表，判断用户是否拥有指定权限
        UserRole byUidAndRoleId = userRoleDao.getByUidAndRoleId(uid, roleEnum.getId());
//        如果没有，返回false
        if(Objects.isNull(byUidAndRoleId)){
            return false;
        }
        return true;
    }
}
