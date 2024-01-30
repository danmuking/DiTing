package com.linyi.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.linyi.common.domain.dto.RequestInfo;
import com.linyi.common.utils.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestInfo info = new RequestInfo();
//        获取用户uid
        info.setUid(Optional.ofNullable(request.getAttribute(TokenInterceptor.ATTRIBUTE_UID)).map(Object::toString).map(Long::parseLong).orElse(null));
//        获取Ip
        info.setIp(ServletUtil.getClientIP(request));
        RequestHolder.set(info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}