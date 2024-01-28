package com.linyi.chat.controller;

import com.linyi.chat.domain.vo.request.IdReqVO;
import com.linyi.chat.domain.vo.response.MemberResp;
import com.linyi.chat.service.RoomAppService;
import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.common.utils.RequestHolder;
import com.linyi.user.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: DiTing
 * @description: 群聊相关接口
 * @author: lin
 * @create: 2024-01-28 21:04
 **/
@RestController
@RequestMapping("/capi/room")
@Api(tags = "聊天室相关接口")
@Slf4j
public class RoomController{
    @Autowired
    private RoomAppService roomService;
    @GetMapping("/public/group")
    @ApiOperation("群组详情")
    public ApiResult<MemberResp> groupDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getGroupDetail(uid, request.getId()));
    }
}
