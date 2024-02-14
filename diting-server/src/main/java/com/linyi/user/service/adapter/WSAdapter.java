package com.linyi.user.service.adapter;

import com.linyi.chat.domain.dto.ChatMsgRecallDTO;
import com.linyi.chat.domain.vo.response.ChatMessageResp;
import com.linyi.user.domain.entity.User;
import com.linyi.user.domain.enums.WSMsgRecall;
import com.linyi.user.domain.enums.WSRespTypeEnum;
import com.linyi.user.domain.vo.response.ws.WSBaseResp;
import com.linyi.user.domain.vo.response.ws.WSFriendApply;
import com.linyi.user.domain.vo.response.ws.WSLoginSuccess;
import com.linyi.user.domain.vo.response.ws.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.BeanUtils;

/**
 * @description websocket消息适配器
 * @date 2024/1/11 16:30
 */
public class WSAdapter {
    /**
     * @param :
     * @return void
     * @description 微信二维码返回适配器
     * @date 2024/1/11 16:31
     */
    public static WSBaseResp buildLoginResp(WxMpQrCodeTicket wxMpQrCodeTicket){
        WSBaseResp<WSLoginUrl> wsLoginUrlWSBaseResp = new WSBaseResp<>();
        wsLoginUrlWSBaseResp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsLoginUrlWSBaseResp.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        return wsLoginUrlWSBaseResp;
    }

    public static WSBaseResp buildLoginSuccessResp(User user,String token) {
        WSBaseResp<WSLoginSuccess> wsLoginUrlWSBaseResp = new WSBaseResp<>();
        wsLoginUrlWSBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        wsLoginUrlWSBaseResp.setData(WSLoginSuccess.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .build());
        return wsLoginUrlWSBaseResp;
    }

    public static WSBaseResp buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> wsLoginUrlWSBaseResp = new WSBaseResp<>();
        wsLoginUrlWSBaseResp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return wsLoginUrlWSBaseResp;
    }

    public static WSBaseResp<WSFriendApply> buildApplySend(WSFriendApply resp) {
        WSBaseResp<WSFriendApply> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.APPLY.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }
}
