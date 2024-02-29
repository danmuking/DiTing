package com.linyi.chat.domain.vo.response.msg;

import com.linyi.common.utils.discover.domain.UrlInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @program: DiTing
 * @description: 文本消息响应
 * @author: lin
 * @create: 2024-01-25 19:07
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgResp {
    @ApiModelProperty("消息内容")
    private String content;
    @ApiModelProperty("消息链接映射")
    private Map<String, UrlInfo> urlContentMap;
//    @ApiModelProperty("艾特的uid")
//    private List<Long> atUidList;
    @ApiModelProperty("父消息，如果没有父消息，返回的是null")
    private TextMsgResp.ReplyMsg reply;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyMsg {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("用户uid")
        private Long uid;
        @ApiModelProperty("用户名称")
        private String username;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容不同的消息类型，见父消息内容体")
        private Object body;
        @ApiModelProperty("是否可消息跳转 0否 1是")
        private Integer canCallback;
        @ApiModelProperty("跳转间隔的消息条数")
        private Integer gapCount;
    }
}

