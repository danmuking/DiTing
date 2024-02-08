package com.linyi.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description: oss场景枚举
 * @author: lin
 * @create: 2024-02-08 20:50
 **/
@AllArgsConstructor
@Getter
public enum OssSceneEnum {
    CHAT(1, "聊天", "/chat"),
    EMOJI(2, "表情包", "/emoji"),
    ;

    private final Integer type;
    private final String desc;
    private final String path;

    private static final Map<Integer, OssSceneEnum> cache;

    static {
        cache = Arrays.stream(OssSceneEnum.values()).collect(Collectors.toMap(OssSceneEnum::getType, Function.identity()));
    }

    public static OssSceneEnum of(Integer type) {
        return cache.get(type);
    }
}