package com.linyi.user.service;

import com.linyi.chat.domain.vo.response.IdRespVO;
import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.user.domain.vo.request.user.UserEmojiReq;
import com.linyi.user.domain.vo.response.UserEmojiResp;

import java.security.PrivateKey;
import java.util.List;

/**
 * @program: DiTing
 * @description: 用户表情包服务
 * @author: lin
 * @create: 2024-02-08 22:04
 **/
public interface UserEmojiService {

    /**
     * @param req:
     * @param uid:
     * @return ApiResult<IdRespVO>
     * @description 新增表情包
     * @date 2024/2/8 22:07
     */
    ApiResult<IdRespVO> insert(UserEmojiReq req, Long uid);

    /**
     * @param uid:
     * @return List<UserEmojiResp>
     * @description 请求表情包列表
     * @date 2024/2/8 22:28
     */
    List<UserEmojiResp> list(Long uid);

    /**
     * @param id:
     * @param uid:
     * @return void
     * @description 删除表情包
     * @date 2024/2/8 22:28
     */
    void remove(long id, Long uid);
}
