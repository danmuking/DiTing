package com.linyi.chat.dao;

import com.linyi.chat.domain.entity.MessageMark;
import com.linyi.chat.mapper.MessageMarkMapper;
import com.linyi.chat.service.IMessageMarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> implements IMessageMarkService {

}
