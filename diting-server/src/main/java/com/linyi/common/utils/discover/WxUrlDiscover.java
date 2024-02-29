package com.linyi.common.utils.discover;

import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

/**
 * @package: com.linyi.common.utils.discover
 * @className: WxUrlDiscover
 * @author: Lin
 * @description: 针对微信公众号的链接解析
 * @date: 2024/2/29 23:32
 * @version: 1.0
 */
public class WxUrlDiscover extends AbstractUrlDiscover {

    @Nullable
    @Override
    public String getTitle(Document document) {
        return document.getElementsByAttributeValue("property", "og:title").attr("content");
    }

    @Nullable
    @Override
    public String getDescription(Document document) {
        return document.getElementsByAttributeValue("property", "og:description").attr("content");
    }

    @Nullable
    @Override
    public String getImage(String url, Document document) {
        String href = document.getElementsByAttributeValue("property", "og:image").attr("content");
        return isConnect(href) ? href : null;
    }
}

