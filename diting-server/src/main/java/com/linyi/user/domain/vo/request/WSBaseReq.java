package com.linyi.user.domain.vo.request;

import lombok.Data;

/**
 * @description websocket前端请求体
 * @date 2024/1/10 23:35
 */
@Data
public class WSBaseReq {
    private Integer type;

    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private String data;
}