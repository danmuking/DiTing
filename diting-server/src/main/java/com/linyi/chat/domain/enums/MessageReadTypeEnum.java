package com.linyi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description: 消息已读未读枚举
 * @author: lin
 * @create: 2024-01-26 18:38
 **/
@AllArgsConstructor
@Getter
public enum MessageReadTypeEnum {
    READ(1L, "已读"),
    UNREAD(2L, "未读"),
    ;

    private final Long type;
    private final String desc;

    private static Map<Long, MessageReadTypeEnum> cache;

    static {
        cache = Arrays.stream(MessageReadTypeEnum.values()).collect(Collectors.toMap(MessageReadTypeEnum::getType, Function.identity()));
    }

    public static MessageReadTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
