package com.linyi.user.domain.vo.response.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description: 好友申请相应
 * @author: lin
 * @create: 2024-01-23 19:35
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyResp {
    @ApiModelProperty("申请id")
    private Long applyId;

    @ApiModelProperty("申请人uid")
    private Long uid;

    @ApiModelProperty("申请类型 1加好友")
    private Integer type;

    @ApiModelProperty("申请信息")
    private String msg;

    @ApiModelProperty("申请状态 1待审批 2同意")
    private Integer status;
}
