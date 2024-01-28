package com.linyi.chat.domain.vo.request;

import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-28 21:42
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReq extends CursorPageBaseReq {
    @ApiModelProperty("房间号")
    private Long roomId = 1L;
}

