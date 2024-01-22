package com.linyi.user.controller;

import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.common.utils.RequestHolder;
import com.linyi.user.domain.vo.request.friend.FriendApplyReq;
import com.linyi.user.service.FriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @package: com.linyi.user.controller
 * @className: FriendController
 * @author: Lin
 * @description: 好友相关接口
 * @date: 2024/1/22 21:52
 * @version: 1.0
 */

@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "好友相关接口")
@Slf4j
public class FriendController {
    @Autowired
    FriendService friendService;
    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq request) {
        Long uid = RequestHolder.get().getUid();
        friendService.apply(uid, request);
        return ApiResult.success();
    }
}
