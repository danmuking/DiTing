package com.linyi.chat.dao;

import com.linyi.chat.domain.entity.MessageMark;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.chat.mapper.MessageMarkMapper;
import com.linyi.chat.service.IMessageMarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.common.domain.enums.NormalOrNoEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public List<MessageMark> getValidMarkByMsgIdBatch(List<Long> msgIdList) {
        return lambdaQuery()
                .in(MessageMark::getMsgId, msgIdList)
                .eq(MessageMark::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .list();
    }
}
