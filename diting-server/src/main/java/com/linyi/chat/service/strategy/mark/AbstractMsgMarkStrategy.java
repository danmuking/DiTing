package com.linyi.chat.service.strategy.mark;

import com.linyi.chat.dao.MessageMarkDao;
import com.linyi.chat.domain.dto.ChatMessageMarkDTO;
import com.linyi.chat.domain.entity.MessageMark;
import com.linyi.chat.domain.enums.MessageMarkActTypeEnum;
import com.linyi.chat.domain.enums.MessageMarkTypeEnum;
import com.linyi.common.domain.enums.YesOrNoEnum;
import com.linyi.common.event.MessageMarkEvent;
import com.linyi.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * @package: com.linyi.chat.service.strategy.mark
 * @className: AbstractMsgMarkStrategy
 * @author: Lin
 * @description: 消息标记处理策略抽象类
 * @date: 2024/1/25 22:28
 * @version: 1.0
 */
public abstract class AbstractMsgMarkStrategy {
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * @return MessageMarkTypeEnum
     * @description 返回消息标记类型
     * @date 2024/1/25 22:29
     */
    protected abstract MessageMarkTypeEnum getTypeEnum();

    @Transactional
    public void mark(Long uid, Long msgId) {
        doMark(uid, msgId);
    }

    @Transactional
    public void unMark(Long uid, Long msgId) {
        doUnMark(uid, msgId);
    }

    /**
     * @param :
     * @return void
     * @description 注册消息标记处理策略
     * @date 2024/1/25 22:29
     */
    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    /**
     * @param uid:
     * @param msgId:
     * @return void
     * @description 执行消息标记处理
     * @date 2024/1/25 22:30
     */
    protected void doMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    /**
     * @param uid:
     * @param msgId:
     * @return void
     * @description 执行取消消息标记处理
     * @date 2024/1/25 22:30
     */
    protected void doUnMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    /**
     * @param uid:
     * @param msgId:
     * @param actTypeEnum:
     * @description 执行消息标记处理
     * @date 2024/1/25 22:35
     */
    protected void exec(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        Integer markType = getTypeEnum().getType();
        Integer actType = actTypeEnum.getType();
//        获取旧的消息标记
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
//        取消标记的类型，数据库一定有记录，没有就直接跳过操作
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UN_MARK) {
            return;
        }
//        插入一条新消息标记,或者修改一条消息标记,
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
        boolean modify = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if (modify) {
            //修改成功才发布消息标记事件
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
        }
    }

    /**
     * @param actType:
     * @return Integer
     * @description 动作类型转换
     * @date 2024/1/25 22:43
     */
    private Integer transformAct(Integer actType) {
        if (actType == 1) {
            return YesOrNoEnum.NO.getStatus();
        } else if (actType == 2) {
            return YesOrNoEnum.YES.getStatus();
        }
        throw new BusinessException("动作类型 1确认 2取消");
    }

}