package com.linyi.user.consumer;

import com.linyi.common.constant.MQConstant;
import com.linyi.user.domain.dto.LoginMessageDTO;
import com.linyi.user.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: DiTing
 * @description: 在本地服务上找寻对应channel，将对应用户登陆，并触发所有用户收到上线事件
 * @author: lin
 * @create: 2024-02-15 11:45
 **/
@RocketMQMessageListener(consumerGroup = MQConstant.LOGIN_MSG_GROUP, topic = MQConstant.LOGIN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class MsgLoginConsumer implements RocketMQListener<LoginMessageDTO> {
    @Autowired
    private WebSocketService webSocketService;
    @Override
    public void onMessage(LoginMessageDTO loginMessageDTO) {
//        尝试登录
        webSocketService.scanLoginSuccess(loginMessageDTO.getCode(), loginMessageDTO.getUid());
    }
}
