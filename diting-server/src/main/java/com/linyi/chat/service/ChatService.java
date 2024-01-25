package com.linyi.chat.service;

import com.linyi.chat.domain.vo.request.ChatMessagePageReq;
import com.linyi.chat.domain.vo.request.ChatMessageReq;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;

import javax.validation.constraints.NotNull;

/**
 * @program: DiTing
 * @description: 消息处理类
 * @author: lin
 * @create: 2024-01-24 20:53
 **/
public interface ChatService {
    /**
     * @param request:
     * @param uid:
     * @return CursorPageBaseResp<ChatMessageResp>
     * @description 获取消息列表
     * @date 2024/1/24 20:55
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request,@NotNull Long uid);

    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * @param msgId:
     * @param uid:
     * @return ChatMessageResp
     * @description 根据消息获取消息前端展示的物料
     * @date 2024/1/25 20:14
     */
    ChatMessageResp getMsgResp(Long msgId, Long uid);
}
