package com.linyi.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.vo.request.ChatMessageReadReq;
import com.linyi.chat.mapper.ContactMapper;
import com.linyi.chat.service.IContactService;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
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

    public CursorPageBaseResp<Contact> getReadPage(Message message, ChatMessageReadReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq, wrapper -> {
            wrapper.eq(Contact::getRoomId, message.getRoomId());
            wrapper.ne(Contact::getUid, message.getFromUid());
            wrapper.ge(Contact::getReadTime, message.getCreateTime());
        }, Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> getUnReadPage(Message message, ChatMessageReadReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq, wrapper -> {
            wrapper.eq(Contact::getRoomId, message.getRoomId());
            wrapper.ne(Contact::getUid, message.getFromUid());// 不需要查询出自己
            wrapper.lt(Contact::getReadTime, message.getCreateTime());// 已读时间小于消息发送时间
        }, Contact::getReadTime);
    }
}
