package com.linyi.chat.controller;


import com.linyi.chat.domain.vo.request.ChatMessageMarkReq;
import com.linyi.chat.domain.vo.request.ChatMessagePageReq;
import com.linyi.chat.domain.vo.request.ChatMessageReq;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.chat.service.ChatService;
import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;
import com.linyi.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 会话列表 前端控制器
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/public/msg/page")
    @ApiOperation("消息列表")
//    @FrequencyControl(time = 120, count = 20, target = FrequencyControl.Target.IP)
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, RequestHolder.get().getUid());
//        filterBlackMsg(msgPage);
        return ApiResult.success(msgPage);
    }

    @PostMapping("/msg")
    @ApiOperation("发送消息")
//    @FrequencyControl(time = 5, count = 3, target = FrequencyControl.Target.UID)
//    @FrequencyControl(time = 30, count = 5, target = FrequencyControl.Target.UID)
//    @FrequencyControl(time = 60, count = 10, target = FrequencyControl.Target.UID)
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        //返回完整消息格式，方便前端展示
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }

    @PutMapping("/msg/mark")
    @ApiOperation("消息标记")
//    @FrequencyControl(time = 10, count = 5, target = FrequencyControl.Target.UID)
    public ApiResult<Void> setMsgMark(@Valid @RequestBody ChatMessageMarkReq request) {
        chatService.setMsgMark(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }
}

