package com.linyi.user.service.impl;

import com.abin.mallchat.transaction.service.MQProducer;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.linyi.common.constant.MQConstant;
import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.dto.LoginMessageDTO;
import com.linyi.user.domain.dto.ScanSuccessMessageDTO;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.UserService;
import com.linyi.user.service.WebSocketService;
import com.linyi.user.service.WxMsgService;
import com.linyi.user.service.adapter.TextBuilder;
import com.linyi.user.service.adapter.UserAdapter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@Slf4j
public class WxMsgServiceImpl implements WxMsgService {
    private static final Cache<String,Integer> USER_OPENID_MAP = Caffeine.newBuilder().maximumSize(10000L).expireAfterWrite(Duration.ofHours(1)).build();
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Value("${wx.mp.callback}")
    private String callback;
    @Autowired
    UserService userService;
    @Autowired
    WebSocketService webSocketService;
    @Autowired
    UserDao userDao;
    @Autowired
    private MQProducer mqProducer;

    @Override
    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {
//        获取open id
        String openId = wxMpXmlMessage.getFromUser();
//        获取事件码
        int code = Integer.parseInt(getEventKey(wxMpXmlMessage));
//        用户登录流程
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StringUtils.isNotEmpty(user.getAvatar());
//        已注册且已授权，直接返回
        if(registered && authorized){
//            登录成功事件,向前端返回登录成功通知
            webSocketService.scanLoginSuccess(code, user.getId());
            return null;
        }
//        未注册，先注册
        else if (!registered){
            user = User.builder().openId(openId).build();
            userService.register(user);
        }
//        绑定事件码和openid
        USER_OPENID_MAP.put(openId,code);
//        请求用户授权
        //授权流程,给用户发送授权消息，并且异步通知前端扫码成功,等待授权
        mqProducer.sendMsg(MQConstant.SCAN_MSG_TOPIC, new ScanSuccessMessageDTO(code));
        String authorizeUrl = String.format(URL,wxMpService.getWxMpConfigStorage().getAppId(),callback + "/wx/portal/public/callBack");
        return new TextBuilder().build("请点击链接授权：<a href=\"" + authorizeUrl + "\">登录</a>", wxMpXmlMessage);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(userInfo.getOpenid());
//        将用户昵称和头像存入数据库
        user = UserAdapter.buildAuthorizeUser(user.getId(), userInfo);
        userDao.updateById(user);
        Integer code = USER_OPENID_MAP.getIfPresent(openid);
//        删除缓存
        USER_OPENID_MAP.invalidate(openid);
//        授权完成，发送登录成功事件
        mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), code));
    }

    /**
     * @param wxMpXmlMessage:
     * @return String
     * @description 获取事件码
     * @date 2024/1/11 20:00
     */
    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        String code = wxMpXmlMessage.getEventKey();
        return code.replace("qrscene_","");
    }
}
