package com.linyi.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @package: com.linyi.chat.domain.vo.request
 * @className: ChatMessageMarkReq
 * @author: Lin
 * @description: 消息标记请求
 * @date: 2024/1/25 22:24
 * @version: 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkReq {
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    @NotNull
    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}

