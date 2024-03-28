package com.linyi.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: DiTing
 * @description: 将扫码登录返回信息推送给所有横向扩展的服务
 * @author: lin
 * @create: 2024-02-15 11:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uid;
    private Integer code;
}
