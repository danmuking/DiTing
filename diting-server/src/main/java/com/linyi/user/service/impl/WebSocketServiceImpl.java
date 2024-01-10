package com.linyi.user.service.impl;

import com.linyi.user.domain.dto.WSChannelExtraDTO;
import com.linyi.user.service.WebSocketService;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @package: com.linyi.user.service.impl
 * @className: WebSocketServiceImpl
 * @author: Lin
 * @description: TODO
 * @date: 2024/1/10 23:07
 * @version: 1.0
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {
    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
//    TODO:导入Caffeine
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine;
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> CHANNELS= new ConcurrentHashMap<>();

    @Override
    public void connect(Channel channel) {
//        现在DTO是空的，因为用户还没扫码，不能绑定用户信息
        CHANNELS.put(channel,new WSChannelExtraDTO());
    }

    @Override
    public void handleLoginReq(Channel channel) {
//        生成一个随机code
        Integer code = generateCode();
    }

    /**
     * 生成一个随机code,并且和已有code不重复
     * @return
     */
    private Integer generateCode() {
    }
}
