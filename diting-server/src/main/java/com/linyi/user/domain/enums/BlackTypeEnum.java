package com.linyi.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-06 21:53
 **/
@AllArgsConstructor
@Getter
public enum BlackTypeEnum {
    IP(1),
    UID(2),
    ;

    private final Integer type;

}

