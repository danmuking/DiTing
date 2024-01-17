package com.linyi.user.controller;


import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.common.utils.RequestHolder;
import com.linyi.user.domain.vo.request.WearingBadgeReq;
import com.linyi.user.domain.vo.response.user.BadgeResp;
import com.linyi.user.domain.vo.response.user.UserInfoResp;
import com.linyi.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lin
 * @since 2024-01-09
 */
@RestController
@RequestMapping("/capi/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }
}

