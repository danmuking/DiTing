package com.linyi.user.domain.vo.request.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @program: DiTing
 * @description: 删除好友请求
 * @author: lin
 * @create: 2024-01-23 19:05
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteReq {
    @NotNull
    @ApiModelProperty("好友uid")
    private Long targetUid;
}
