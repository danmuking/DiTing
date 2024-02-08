package com.linyi.user.service;

import com.abin.mallchat.oss.domain.OssResp;
import com.linyi.user.domain.vo.request.UploadUrlReq;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-08 20:42
 **/
public interface OssService {
    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
