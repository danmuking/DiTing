package com.linyi.common.utils.discover;

import cn.hutool.core.date.StopWatch;
import com.linyi.common.utils.discover.domain.UrlInfo;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @package: com.linyi.common.utils.discover
 * @className: UrlDiscover
 * @author: Lin
 * @description: url解析接口
 * @date: 2024/2/29 23:15
 * @version: 1.0
 */
public interface UrlDiscover {
    @Nullable
    Map<String, UrlInfo> getUrlContentMap(String content);

    /**
     * @param url:
     * @return UrlInfo
     * @description 获取连接信息
     * @date 2024/2/29 23:26
     */
    @Nullable
    UrlInfo getContent(String url);

    /**
     * @param document:
     * @return String
     * @description 获取url标题
     * @date 2024/2/29 23:17
     */
    @Nullable
    String getTitle(Document document);

    @Nullable
    String getDescription(Document document);

    /**
     * @param url:
     * @param document:
     * @return String
     * @description 获取url图片
     * @date 2024/2/29 23:17
     */
    @Nullable
    String getImage(String url, Document document);
}
