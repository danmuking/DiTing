package com.linyi.chat.service.strategy.mark;

import com.linyi.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @package: com.linyi.chat.service.strategy.mark
 * @className: DisLikeStrategy
 * @author: Lin
 * @description: 点踩标记策略类
 * @date: 2024/1/25 22:44
 * @version: 1.0
 */
@Component
public class DisLikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        //同时取消点赞的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
    }

}
