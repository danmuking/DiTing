package com.linyi.chat.service.strategy.msg;

import com.linyi.common.exception.CommonErrorEnum;
import com.linyi.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @package: com.linyi.chat.service.strategy.msg
 * @className: MsgHandlerFactory
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/24 22:53
 * @version: 1.0
 */
public class MsgHandlerFactory {
    private static final Map<Integer, AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code, AbstractMsgHandler strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractMsgHandler getStrategyNoNull(Integer code) {
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
