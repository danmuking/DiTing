package com.linyi.chat.service;

import com.linyi.chat.domain.dto.MsgReadInfoDTO;
import com.linyi.chat.domain.vo.request.*;
import com.linyi.chat.domain.vo.response.ChatMessageReadResp;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;

import javax.validation.constraints.NotNull;
import java.util.Collection;

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

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 设置消息标记
     * @date 2024/1/25 22:26
     */
    void setMsgMark(Long uid, ChatMessageMarkReq request);

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 消息撤回
     * @date 2024/1/26 18:05
     */
    void recallMsg(Long uid, ChatMessageBaseReq request);

    CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, ChatMessageReadReq request);

    Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request);

    void msgRead(Long uid, ChatMessageMemberReq request);
}
