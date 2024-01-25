package com.linyi.chat.service.strategy.mark;

import com.linyi.common.exception.CommonErrorEnum;
import com.linyi.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @package: com.linyi.chat.service.strategy.mark
 * @className: MsgMarkFactory
 * @author: Lin
 * @description: 消息标记策略工厂
 * @date: 2024/1/25 22:31
 * @version: 1.0
 */
public class MsgMarkFactory {
    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer markType, AbstractMsgMarkStrategy strategy) {
        STRATEGY_MAP.put(markType, strategy);
    }

    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType) {
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}