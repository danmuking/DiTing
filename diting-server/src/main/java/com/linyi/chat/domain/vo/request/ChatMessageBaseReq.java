package com.linyi.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @program: DiTing
 * @description: 消息基础请求
 * @author: lin
 * @create: 2024-01-26 18:03
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageBaseReq {
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}
