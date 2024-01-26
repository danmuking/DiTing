package com.linyi.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: DiTing
 * @description: 消息撤回推送类
 * @author: lin
 * @create: 2024-01-26 18:24
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRecallDTO {
    private Long msgId;
    private Long roomId;
    //撤回的用户
    private Long recallUid;
}

