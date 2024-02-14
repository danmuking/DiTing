package com.linyi.chat.service.cache;

import com.linyi.chat.dao.MessageDao;
import com.linyi.chat.domain.entity.Message;
import com.linyi.common.constant.RedisKey;
import com.linyi.common.service.cache.AbstractJ2Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-14 15:46
 **/
@Component
public class MessageCache extends AbstractJ2Cache<Long, Message> {
    @Autowired
    private MessageDao messageDao;
    @Override
    protected String getKey(Long req) {
        return RedisKey.getKey(RedisKey.MESSAGE,req);
    }

    @Override
    protected Map<Long, Message> load(List<Long> req) {
        return messageDao.getByIds(req).stream().collect(Collectors.toMap(Message::getId, Function.identity()));
    }
}
