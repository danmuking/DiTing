package com.linyi.user.service.handler;

import com.linyi.user.service.WxMsgService;
import com.linyi.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description 用户关注事件
 * @date 2024/1/11 19:35
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private WxMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
//        TODO:没理解，这里应该判断是关注是单纯关注还是为了登录，它们之间有什么区别。
        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = this.handleSpecial(weixinService, wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

        try {
            return new TextBuilder().build("感谢关注", wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpService weixinService, WxMpXmlMessage wxMessage)
            throws Exception {
        return wxMsgService.scan(weixinService, wxMessage);
    }

}
