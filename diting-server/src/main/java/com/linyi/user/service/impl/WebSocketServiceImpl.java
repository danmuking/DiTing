package com.linyi.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.linyi.common.config.ThreadPoolConfig;
import com.linyi.common.event.UserOfflineEvent;
import com.linyi.common.event.UserOnlineEvent;
import com.linyi.common.utils.JwtUtils;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.dto.WSChannelExtraDTO;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.RoleEnum;
import com.linyi.user.domain.vo.request.user.WSAuthorize;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import com.linyi.user.service.IRoleService;
import com.linyi.user.service.LoginService;
import com.linyi.user.service.WebSocketService;
import com.linyi.user.service.adapter.WSAdapter;
import com.linyi.user.service.cache.UserCache;
import com.linyi.websocket.NettyUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @package: com.linyi.user.service.impl
 * @className: WebSocketServiceImpl
 * @author: Lin
 * @description: WebSocketServiceImpl实现类
 * @date: 2024/1/10 23:07
 * @version: 1.0
 */
@Slf4j
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
    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

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

    @Autowired
    private UserCache userCache;
    @Autowired
    private IRoleService iRoleService;

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
//        向前端发送
        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    public void userOffline(ChannelHandlerContext channel) {
        removed(channel.channel());
        channel.channel().close();
    }

    private void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_CHANNEL.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {//已登录用户断连,并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    private boolean offline(Channel channel, Optional<Long> uidOptional) {
//        移除channel
        ONLINE_CHANNEL.remove(channel);
//        移除用户列表对应通道
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
//        将用户和Channel绑定
//        根据事件码获取channel
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(channel == null){
            return;
        }
        User user = userCache.getUserInfo(uid);
//        移除事件码
        WAIT_LOGIN_MAP.invalidate(code);
//        生成token
        String token = loginService.login(uid);
        loginSuccess(channel, user, token);
    }

    private void loginSuccess(Channel channel, User user, String token) {
        //更新上线列表
        online(channel, user.getId());
        //返回给用户登录成功
        boolean hasPower = iRoleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        //发送给对应的用户
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token, hasPower));
        //发送用户上线事件
        boolean online = userCache.isOnline(user.getId());
        if (!online) {
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtils.getAttr(channel, NettyUtils.IP));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    /**
     * @param channel:
     * @param uid:
     * @return void
     * @description 更新在线用户列表
     * @date 2024/2/15 12:39
     */
    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtils.setAttr(channel, NettyUtils.UID, uid);
    }

    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_CHANNEL.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_CHANNEL.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
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

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseMsg));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseMsg, Long skipUid) {
        ONLINE_CHANNEL.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseMsg));
        });
    }

    @Override
    public Boolean scanSuccess(Integer loginCode) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.nonNull(channel)) {
            sendMsg(channel, WSAdapter.buildScanSuccessResp());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void sendMsg(Channel channel, WSBaseResp wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    /**
     * 生成一个随机code,并且和已有code不重复
     * TODO: 性能更高的实现
     * @return
     */
    private Integer generateCode(Channel channel) {
        int code;
        do{
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }while(WAIT_LOGIN_MAP.asMap().containsKey(code));
//        将channel和code绑定
        WAIT_LOGIN_MAP.put(code,channel);
        return code;
    }
}
