package com.linyi.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @package: com.linyi.user.domain.enums
 * @className: HotFlagEnum
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/22 22:57
 * @version: 1.0
 */
@AllArgsConstructor
@Getter
public enum HotFlagEnum {
    NOT(0, "非热点"),
    YES(1, "热点"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, HotFlagEnum> cache;

    static {
        cache = Arrays.stream(HotFlagEnum.values()).collect(Collectors.toMap(HotFlagEnum::getType, Function.identity()));
    }

    public static HotFlagEnum of(Integer type) {
        return cache.get(type);
    }
}

