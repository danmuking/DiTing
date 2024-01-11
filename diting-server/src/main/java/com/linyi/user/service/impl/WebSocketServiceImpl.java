package com.linyi.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.linyi.user.domain.dto.WSChannelExtraDTO;
import com.linyi.user.domain.vo.request.WSBaseReq;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import com.linyi.user.service.WebSocketService;
import com.linyi.user.service.adapter.WSAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @package: com.linyi.user.service.impl
 * @className: WebSocketServiceImpl
 * @author: Lin
 * @description: WebSocketServiceImpl实现类
 * @date: 2024/1/10 23:07
 * @version: 1.0
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {
    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * @description 关联channel和事件码
     * @date 2024/1/11 16:00
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAX_MUM_SIZE)
            .expireAfterWrite(EXPIRE_TIME)
            .build();
    @Autowired
    WxMpService wxMpService;
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> CHANNELS= new ConcurrentHashMap<>();

    @Override
    public void connect(Channel channel) {
//        现在DTO是空的，因为用户还没扫码，不能绑定用户信息
        CHANNELS.put(channel,new WSChannelExtraDTO());
    }

    @Override
    public void handleLoginReq(Channel channel) throws WxErrorException {
//        生成一个随机code
        Integer code = generateCode(channel);
//        向微信请求二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
//        向前端发送
        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    @Override
    public void userOffline(Channel channel) {
        CHANNELS.remove(channel);
    }

    private void sendMsg(Channel channel, WSBaseResp wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    /**
     * 生成一个随机code,并且和已有code不重复
     * TODO: 性能更高的实现
     * @return
     */
    private synchronized Integer generateCode(Channel channel) {
        int code;
        do{
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }while(WAIT_LOGIN_MAP.asMap().containsKey(code));
        WAIT_LOGIN_MAP.put(code,channel);
        return code;
    }
}
