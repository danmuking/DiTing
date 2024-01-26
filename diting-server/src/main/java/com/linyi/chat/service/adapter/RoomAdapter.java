package com.linyi.chat.service.adapter;

import com.linyi.chat.domain.entity.Contact;
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
}
