package com.linyi.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.chat.domain.dto.MsgReadInfoDTO;
import com.linyi.chat.domain.entity.Contact;
import com.linyi.chat.domain.entity.Message;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会话列表 服务类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
public interface ContactService {

    Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages);
}
