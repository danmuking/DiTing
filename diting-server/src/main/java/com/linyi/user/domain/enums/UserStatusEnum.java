package com.linyi.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @package: com.linyi.user.domain.enums
 * @className: UserStatusEnum
 * @author: Lin
 * @description: 用户状态枚举
 * @date: 2024/1/21 23:06
 * @version: 1.0
 */
@AllArgsConstructor
@Getter
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    BLACK(1, "拉黑"),
    ;
    private Integer status;
    private String desc;
    private static Map<Integer, UserStatusEnum> cache;
    static {
        cache = Arrays.stream(UserStatusEnum.values()).collect(Collectors.toMap(UserStatusEnum::getStatus, Function.identity()));
    }

    public static UserStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
