package com.linyi.chat.service;

import com.linyi.chat.domain.vo.response.ChatRoomResp;
import com.linyi.chat.domain.vo.response.MemberResp;
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

    /**
     * @param uid:
     * @param roomId:
     * @return ChatRoomResp
     * @description 获取群聊会话详情
     * @date 2024/1/27 14:35
     */
    ChatRoomResp getContactDetail(Long uid, long roomId);

    /**
     * @param uid:
     * @param uid1:
     * @return Object
     * @description 获取单聊会话详情
     * @date 2024/1/27 14:41
     */
    ChatRoomResp getContactDetailByFriend(Long uid, Long uid1);

    /**
     * @param uid:
     * @param id:
     * @return MemberResp
     * @description 获取群聊详情
     * @date 2024/1/28 21:10
     */
    MemberResp getGroupDetail(Long uid, long id);
}
