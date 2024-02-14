package com.linyi.common.service.cache;

import com.linyi.chat.dao.MessageDao;
import com.linyi.chat.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-14 16:33
 **/
@Component
public class MsgCache {
    @Autowired
    private MessageDao messageDao;
    @Cacheable(cacheNames = "msg", key = "'msg'+#msgId")
    public Message getMsg(Long msgId) {
        return messageDao.getById(msgId);
    }

    @CacheEvict(cacheNames = "msg", key = "'msg'+#msgId")
    public Message evictMsg(Long msgId) {
        return null;
    }
}
