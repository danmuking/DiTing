package com.linyi.user.domain.vo.response.ws;

import com.linyi.user.domain.enums.WSRespTypeEnum;
import lombok.Data;

@Data
public class WSBaseResp<T> {
    /**
     * ws推送给前端的消息
     *
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
