package com.linyi.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @program: DiTing
 * @description: 添加管理员请求
 * @author: lin
 * @create: 2024-01-29 19:52
 **/
@Data
public class AdminAddReq {
    @NotNull
    @ApiModelProperty("房间号")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 3)
    @ApiModelProperty("需要添加管理的列表")
    private List<Long> uidList;
}

