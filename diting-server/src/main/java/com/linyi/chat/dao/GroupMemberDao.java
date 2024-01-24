package com.linyi.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.mapper.GroupMemberMapper;
import com.linyi.chat.service.IGroupMemberService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> implements IGroupMemberService {

}
