package com.linyi.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @package: com.linyi.chat.domain.dto
 * @className: ChatMessageMarkDTO
 * @author: Lin
 * @description: 消息标记请求
 * @date: 2024/1/25 22:40
 * @version: 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkDTO {
    @ApiModelProperty("操作者")
    private Long uid;

    @ApiModelProperty("消息id")
    private Long msgId;

    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}

