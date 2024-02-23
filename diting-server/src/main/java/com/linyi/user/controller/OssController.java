package com.linyi.user.controller;

import com.abin.mallchat.oss.domain.OssResp;
import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.util.RequestHolder;
import com.linyi.user.domain.vo.request.UploadUrlReq;
import com.linyi.user.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: DiTing
 * @description: oss
 * @author: lin
 * @create: 2024-02-08 20:39
 **/

@RestController
@RequestMapping("/capi/oss")
@Api(tags = "oss相关接口")
public class OssController {
    @Autowired
    private OssService ossService;

    @GetMapping("/upload/url")
    @ApiOperation("获取临时上传链接")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        return ApiResult.success(ossService.getUploadUrl(RequestHolder.get().getUid(), req));
    }
}
