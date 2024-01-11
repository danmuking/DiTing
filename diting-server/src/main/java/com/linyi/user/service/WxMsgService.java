package com.linyi.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface WxMsgService {

    /**
     * @param wxMpService:
     * @param wxMpXmlMessage:
     * @return WxMpXmlOutMessage
     * @description 用户扫码登录
     * @date 2024/1/11 20:15
     */
    WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage);

    /**
     * @param userInfo:
     * @return void
     * @description 用户授权信息保存
     * @date 2024/1/11 20:16
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
