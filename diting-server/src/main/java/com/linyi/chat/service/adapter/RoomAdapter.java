package com.linyi.chat.service.adapter;

import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.GroupMember;
import com.linyi.chat.domain.enums.GroupRoleEnum;
import com.linyi.chat.domain.vo.response.ChatMessageReadResp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-26 18:45
 **/
public class RoomAdapter {
    public static List<ChatMessageReadResp> buildReadResp(List<Contact> list) {
        return list.stream().map(contact -> {
            ChatMessageReadResp resp = new ChatMessageReadResp();
            resp.setUid(contact.getUid());
            return resp;
        }).collect(Collectors.toList());
    }

    public static List<GroupMember> buildGroupMemberBatch(List<Long> uidList, Long groupId) {
        return uidList.stream()
                .distinct()
                .map(uid -> {
                    GroupMember member = new GroupMember();
                    member.setRole(GroupRoleEnum.MEMBER.getType());
                    member.setUid(uid);
                    member.setGroupId(groupId);
                    return member;
                }).collect(Collectors.toList());
    }
}
