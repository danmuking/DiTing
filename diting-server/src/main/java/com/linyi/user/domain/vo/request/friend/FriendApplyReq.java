package com.linyi.user.domain.vo.request.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @package: com.linyi.user.domain.vo.request.friend
 * @className: FriendApplyReq
 * @author: Lin
 * @description: 申请好友信息
 * @date: 2024/1/22 21:59
 * @version: 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyReq {

    @NotBlank
    @ApiModelProperty("申请信息")
    private String msg;

    @NotNull
    @ApiModelProperty("好友uid")
    private Long targetUid;
}
