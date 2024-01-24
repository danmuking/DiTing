package com.linyi.user.domain.vo.request.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: DiTing
 * @description: 好友关系判断响应
 * @author: lin
 * @create: 2024-01-24 19:44
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendCheckResp {
    @ApiModelProperty("校验结果")
    private List<FriendCheck> checkedList;

    @Data
    public static class FriendCheck {
        private Long uid;
        private Boolean isFriend;
    }
}
