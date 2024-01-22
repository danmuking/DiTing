package com.linyi.user.service;

import com.linyi.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author lin
 * @since 2024-01-21
 */
public interface IRoleService  {

    /**
     * @param uid:
     * @param roleEnum:
     * @return boolean
     * @description 判断用户是否拥有指定权限
     * @date 2024/1/21 22:53
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
