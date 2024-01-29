package com.linyi.chat.service;

import com.linyi.chat.domain.vo.request.ChatMessageMemberReq;
import com.linyi.chat.domain.vo.request.MemberDelReq;
import com.linyi.chat.domain.vo.request.MemberReq;
import com.linyi.chat.domain.vo.response.ChatMemberListResp;
import com.linyi.chat.domain.vo.response.ChatMemberResp;
import com.linyi.chat.domain.vo.response.ChatRoomResp;
import com.linyi.chat.domain.vo.response.MemberResp;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;

import java.util.List;

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

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    /**
     * @param request:
     * @return List<ChatMemberListResp>
     * @description 获取成员列表
     * @date 2024/1/29 18:18
     */
    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 移除群成员
     * @date 2024/1/29 18:32
     */
    void delMember(Long uid, MemberDelReq request);
}
