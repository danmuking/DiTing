package com.linyi.interceptor;

import com.linyi.common.exception.HttpErrorEnum;
import com.linyi.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * @package: com.linyi.interceptor
 * @className: TokenInterceptor
 * @author: Lin
 * @description: token拦截器
 * @date: 2024/1/15 23:13
 * @version: 1.0
 */

@Component
public class TokenInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";
    @Autowired
    LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        获取用户登录token
        String token = getToken(request);
//        验证token是否有效
        Long validUid = loginService.getValidUid(token);
//        有登录态,将token设置到ThreadLocal中
        if (Objects.nonNull(validUid)) {
            request.setAttribute(ATTRIBUTE_UID, validUid);
        }
//        无登录态
        else {
//            判断是否是公共路径
            boolean isPublicURI = isPublicURI(request.getRequestURI());
//            不是公共路径，直接返回401
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }


        return Boolean.TRUE;
    }

    /*
     * @param requestURI:
      * @return boolean
     * @description 判断是否是公共路径
     * @date 2024/1/15 23:24
     */
    private boolean isPublicURI(String requestURI) {
        String[] split = requestURI.split("/");
//        判断是否是公共路径
        return split.length > 2 && "public".equals(split[3]);
    }

    /**
     * @param request:
     * @return String
     * @description 获取用户token
     * @date 2024/1/15 23:16
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
//        存在token则返回，不存在则返回null
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);
    }
}
