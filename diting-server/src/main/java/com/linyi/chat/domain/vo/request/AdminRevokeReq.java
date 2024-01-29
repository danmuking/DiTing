package com.linyi.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-29 19:59
 **/
@Data
public class AdminRevokeReq {
    @NotNull
    @ApiModelProperty("房间号")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 3)
    @ApiModelProperty("需要撤销管理的列表")
    private List<Long> uidList;
}
