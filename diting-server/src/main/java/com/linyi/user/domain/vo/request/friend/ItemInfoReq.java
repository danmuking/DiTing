package com.linyi.user.domain.vo.request.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-05 22:17
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoReq {
    @ApiModelProperty(value = "徽章信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @ApiModelProperty(value = "徽章id")
        private Long itemId;
        @ApiModelProperty(value = "最近一次更新徽章信息时间")
        private Long lastModifyTime;
    }
}
