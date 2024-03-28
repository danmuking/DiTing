package com.linyi.user.domain.enums;

import com.linyi.chat.domain.vo.response.ChatMemberResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-15 12:46
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {
    private List<ChatMemberResp> changeList = new ArrayList<>();//新的上下线用户
    private Long onlineNum;//在线人数
}
