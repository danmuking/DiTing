package com.linyi.chat.service.strategy.mark;

import com.linyi.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @package: com.linyi.chat.service.strategy.mark
 * @className: LikeStrategy
 * @author: Lin
 * @description: 点赞标记策略类
 * @date: 2024/1/25 22:46
 * @version: 1.0
 */
@Component
public class LikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.LIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        //同时取消点踩的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.DISLIKE.getType()).unMark(uid, msgId);
    }
}
