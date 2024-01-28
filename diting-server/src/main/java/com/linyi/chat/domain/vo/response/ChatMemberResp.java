package com.linyi.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: DiTing
 * @description: 群成员列表响应
 * @author: lin
 * @create: 2024-01-28 21:42
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {
    @ApiModelProperty("uid")
    private Long uid;

    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;

    /**
     * 角色ID
     */
    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;
}
