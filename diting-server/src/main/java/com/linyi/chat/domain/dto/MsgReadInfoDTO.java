package com.linyi.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-26 19:09
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgReadInfoDTO {
    @ApiModelProperty("消息id")
    private Long msgId;

    @ApiModelProperty("已读数")
    private Integer readCount;

    @ApiModelProperty("未读数")
    private Integer unReadCount;

}

