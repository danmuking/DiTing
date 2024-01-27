package com.linyi.chat.service;

import com.linyi.chat.domain.vo.response.ChatRoomResp;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-27 10:29
 **/
public interface RoomAppService {
    /**
     * @param request:
     * @param uid:
     * @return CursorPageBaseResp<ChatRoomResp>
     * @description 获取会话列表
     * @date 2024/1/26 22:29
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);
}
