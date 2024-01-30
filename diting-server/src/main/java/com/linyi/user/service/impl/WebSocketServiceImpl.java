package com.linyi.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.linyi.common.event.UserOnlineEvent;
import com.linyi.common.utils.JwtUtils;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.dto.WSChannelExtraDTO;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.vo.request.user.WSAuthorize;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import com.linyi.user.service.LoginService;
import com.linyi.user.service.WebSocketService;
import com.linyi.user.service.adapter.WSAdapter;
import com.linyi.websocket.NettyUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
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

    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_CHANNEL = new ConcurrentHashMap<>();

    @Lazy
    @Autowired
    WxMpService wxMpService;
    @Autowired
    UserDao userDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void connect(Channel channel) {
//        现在DTO是空的，因为用户还没扫码，不能绑定用户信息
        ONLINE_CHANNEL.put(channel,new WSChannelExtraDTO());
    }

    @Override
    public void handleLoginReq(Channel channel) throws WxErrorException {
//        生成一个随机code
        Integer code = generateCode(channel);
//        向微信请求二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
//        将code和channel绑定
        WAIT_LOGIN_MAP.put(code,channel);
//        向前端发送
        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    @Override
    public void userOffline(Channel channel) {
        ONLINE_CHANNEL.remove(channel);
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
//        将用户和Channel绑定
//        根据事件码获取channel
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(channel == null){
            return;
        }
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_CHANNEL.get(channel);
        wsChannelExtraDTO.setUid(uid);
//        绑定后删除事件码
        ONLINE_CHANNEL.put(channel,wsChannelExtraDTO);
        WAIT_LOGIN_MAP.invalidate(code);

        User user = userDao.getById(uid);
//        生成token
        String token = loginService.login(uid);
//        向前端返回通知
        sendMsg(channel,WSAdapter.buildLoginSuccessResp(user,token));
//        发布事件
        user.setLastOptTime(new Date());
//        刷新ip
        user.refreshIp(NettyUtils.getAttr(channel, NettyUtils.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this,user));
    }

    @Override
    public void sendAuthorizeMsg(int code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(channel == null){
            return;
        }
        sendMsg(channel,WSAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        String token = wsAuthorize.getToken();
//        判断token是否有效
        boolean verify = loginService.verify(token);
        Long uidOrNull = jwtUtils.getUidOrNull(token);
//        如果token有效，就将用户和channel绑定
        if(verify&&Objects.nonNull(uidOrNull)){
            WSChannelExtraDTO wsChannelExtraDTO = ONLINE_CHANNEL.get(channel);
            wsChannelExtraDTO.setUid(uidOrNull);
            ONLINE_CHANNEL.put(channel,wsChannelExtraDTO);
        }
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
