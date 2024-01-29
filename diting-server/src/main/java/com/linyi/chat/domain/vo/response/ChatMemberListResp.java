package com.linyi.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description: 群成员列表的成员消息
 * @author: lin
 * @create: 2024-01-29 18:17
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberListResp {
    @ApiModelProperty("uid")
    private Long uid;
    @ApiModelProperty("用户名称")
    private String name;
    @ApiModelProperty("头像")
    private String avatar;
}
