package com.linyi.chat.service.impl;

import com.linyi.chat.dao.ContactDao;
import com.linyi.chat.domain.dto.MsgReadInfoDTO;
import com.linyi.chat.domain.entity.Message;
import com.linyi.chat.service.ContactService;
import com.linyi.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-01-26 19:16
 **/
@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    ContactDao contactDao;
    /**
     * @param messages
     * @return
     */
    @Override
    public Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages) {
        Map<Long, List<Message>> roomGroup = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtil.equal(roomGroup.size(), 1, "只能查相同房间下的消息");
        Long roomId = roomGroup.keySet().iterator().next();
//        当前房间的总阅读数
        Integer totalCount = contactDao.getTotalCount(roomId);
        return messages.stream().map(message -> {
            MsgReadInfoDTO readInfoDTO = new MsgReadInfoDTO();
            readInfoDTO.setMsgId(message.getId());
//            当前房间阅读时间在消息发出之后的数量就是已读数，未读数就是总数减去已读数减去自己
            Integer readCount = contactDao.getReadCount(message);
            readInfoDTO.setReadCount(readCount);
            readInfoDTO.setUnReadCount(totalCount - readCount - 1);
            return readInfoDTO;
        }).collect(Collectors.toMap(MsgReadInfoDTO::getMsgId, Function.identity()));
    }
}
