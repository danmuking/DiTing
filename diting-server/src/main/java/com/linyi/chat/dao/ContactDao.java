package com.linyi.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.domain.vo.request.ChatMessageReadReq;
import com.linyi.chat.mapper.ContactMapper;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public Integer getTotalCount(Long roomId) {
        return lambdaQuery()
                .eq(Contact::getRoomId, roomId)
                .count();
    }

    public Integer getReadCount(Message message) {
        return lambdaQuery()
                .eq(Contact::getRoomId, message.getRoomId())
                .ne(Contact::getUid, message.getFromUid())// 不需要查询出自己
                .ge(Contact::getReadTime, message.getCreateTime())
                .count();
    }

    public CursorPageBaseResp<Contact> getContactPage(Long uid, CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Contact::getUid, uid);
        }, Contact::getActiveTime);
    }

    public List<Contact> getByRoomIds(List<Long> roomIds, Long uid) {
        return lambdaQuery()
                .in(Contact::getRoomId, roomIds)
                .eq(Contact::getUid, uid)
                .list();
    }

    public Boolean removeByRoomId(Long roomId, List uidList) {
        if (CollectionUtil.isNotEmpty(uidList)) {
            LambdaQueryWrapper<Contact> wrapper = new QueryWrapper<Contact>().lambda()
                    .eq(Contact::getRoomId, roomId)
                    .in(Contact::getUid, uidList);
            return this.remove(wrapper);
        }
        return false;
    }

    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date activeTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, activeTime);
    }
}
