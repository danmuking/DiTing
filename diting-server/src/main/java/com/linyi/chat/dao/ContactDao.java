package com.linyi.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.mapper.ContactMapper;
import com.linyi.chat.service.IContactService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact>{

    public Contact get(Long uid, Long roomId) {
        return lambdaQuery()
                .eq(Contact::getUid, uid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }
}
