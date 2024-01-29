package com.linyi.chat.service.adapter;

import com.linyi.chat.domain.vo.response.ChatMemberListResp;
import com.linyi.chat.domain.vo.response.ChatMemberResp;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.WSRespTypeEnum;
import com.linyi.user.domain.vo.request.user.WSMemberChange;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.linyi.user.domain.vo.request.user.WSMemberChange.CHANGE_TYPE_REMOVE;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-28 21:58
 **/
public class MemberAdapter {
    public static List<ChatMemberResp> buildMember(List<User> list) {
        return list.stream().map(a -> {
            ChatMemberResp resp = new ChatMemberResp();
            resp.setActiveStatus(a.getActiveStatus());
            resp.setLastOptTime(a.getLastOptTime());
            resp.setUid(a.getId());
            return resp;
        }).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buildMemberList(List<User> memberList) {
        return memberList.stream()
                .map(a -> {
                    ChatMemberListResp resp = new ChatMemberListResp();
                    BeanUtils.copyProperties(a, resp);
                    resp.setUid(a.getId());
                    return resp;
                }).collect(Collectors.toList());
    }

    public static WSBaseResp<WSMemberChange> buildMemberRemoveWS(Long roomId, Long uid) {
        WSBaseResp<WSMemberChange> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setUid(uid);
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(CHANGE_TYPE_REMOVE);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }
}
