package com.linyi.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-28 21:05
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResp {
    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("群名称")
    private String groupName;
    @ApiModelProperty("群头像")
    private String avatar;
    @ApiModelProperty("在线人数")
    private Long onlineNum;//在线人数
    @ApiModelProperty("成员角色 1群主 2管理员 3普通成员 4踢出群聊")
    private Integer role;
}

