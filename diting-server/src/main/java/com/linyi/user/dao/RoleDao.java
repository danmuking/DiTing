package com.linyi.user.dao;

import com.linyi.user.domain.entity.Role;
import com.linyi.user.domain.enums.RoleEnum;
import com.linyi.user.mapper.RoleMapper;
import com.linyi.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-21
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
