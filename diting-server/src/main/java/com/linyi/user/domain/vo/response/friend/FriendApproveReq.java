package com.linyi.user.domain.vo.response.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @package: com.linyi.user.domain.vo.response.friend
 * @className: FriendApproveReq
 * @author: Lin
 * @description: 申请好友信息
 * @date: 2024/1/22 22:12
 * @version: 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApproveReq {

    @NotNull
    @ApiModelProperty("申请id")
    private Long applyId;

}
