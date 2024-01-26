package com.linyi.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-26 19:08
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadInfoReq {
    @ApiModelProperty("消息id集合（只查本人）")
    @Size(max = 20)
    private List<Long> msgIds;
}
