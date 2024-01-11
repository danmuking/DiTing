package com.linyi.user.service.handler;

import com.linyi.user.service.WxMsgService;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScanHandler extends AbstractHandler {


    @Autowired
    private WxMsgService wxMsgService;

    /**
     * @param wxMpXmlMessage:
     * @param map:
     * @param wxMpService:
     * @param wxSessionManager:
     * @return WxMpXmlOutMessage
     * @description 扫码事件处理
     * @date 2024/1/10 22:37
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        WxMpXmlOutMessage scan = wxMsgService.scan(wxMpService, wxMpXmlMessage);
        return scan;
    }

}
