package com.linyi.user.service.impl;

import com.linyi.user.dao.UserDao;
import com.linyi.user.domain.entity.User;
import com.linyi.user.service.UserService;
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

import java.util.Objects;

@Service
@Slf4j
public class WxMsgServiceImpl implements WxMsgService {
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Value("${wx.mp.callback}")
    private String callback;
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
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
            return null;
        }
//        未注册，先注册
        else if (!registered){
            user = User.builder().openId(openId).build();
            userService.register(user);
        }
//        授权流程
        String authorizeUrl = String.format(URL,wxMpService.getWxMpConfigStorage().getAppId(),callback + "/wx/portal/public/callBack");
        return new TextBuilder().build("请点击链接授权：<a href=\"" + authorizeUrl + "\">登录</a>", wxMpXmlMessage);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        User user = userDao.getByOpenId(userInfo.getOpenid());
//        将用户昵称和头像存入数据库
        user = UserAdapter.buildAuthorizeUser(user.getId(), userInfo);
        userDao.updateById(user);

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
