package com.linyi.user.service.impl;

import com.abin.mallchat.oss.MinIOTemplate;
import com.abin.mallchat.oss.domain.OssReq;
import com.abin.mallchat.oss.domain.OssResp;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.domain.enums.OssSceneEnum;
import com.linyi.user.domain.vo.request.UploadUrlReq;
import com.linyi.user.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-08 20:48
 **/
@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private MinIOTemplate minIOTemplate;
    /**
     * 获取临时的上传链接
     *
     * @param uid
     * @param req
     */
    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
