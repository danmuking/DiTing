package com.linyi.user.domain.vo.response.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description: 好友申请未读数响应
 * @author: lin
 * @create: 2024-01-23 19:55
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendUnreadResp {

    @ApiModelProperty("申请列表的未读数")
    private Integer unReadCount;

}
