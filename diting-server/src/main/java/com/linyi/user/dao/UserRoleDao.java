package com.linyi.user.dao;

import com.linyi.user.domain.entity.UserRole;
import com.linyi.user.mapper.UserRoleMapper;
import com.linyi.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-21
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    /**
     * @param uid:
     * @param roleId:
     * @return void
     * @description 根据uid和roleId获取记录
     * @date 2024/1/21 22:56
     */
    public UserRole getByUidAndRoleId(Long uid, Long roleId) {
        return lambdaQuery()
                .eq(UserRole::getUid, uid)
                .eq(UserRole::getRoleId, roleId)
                .last("limit 1")
                .one();
    }
}
