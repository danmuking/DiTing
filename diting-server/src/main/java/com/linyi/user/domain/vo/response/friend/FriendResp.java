package com.linyi.user.domain.vo.response.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description: 好友响应
 * @author: lin
 * @create: 2024-01-23 20:01
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp {

    @ApiModelProperty("好友uid")
    private Long uid;

    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;
}

