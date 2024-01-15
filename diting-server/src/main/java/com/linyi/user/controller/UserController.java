package com.linyi.user.controller;


import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lin
 * @since 2024-01-09
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

//    @GetMapping("/userInfo")
//    @ApiOperation("用户详情")
//    public ApiResult<UserInfoResp> getUserInfo() {
//        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
//    }
}

