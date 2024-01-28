package com.linyi.chat.service.adapter;

import com.linyi.chat.domain.vo.response.ChatMemberResp;
import com.linyi.user.domain.entity.User;

import java.util.List;
import java.util.stream.Collectors;

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
}
