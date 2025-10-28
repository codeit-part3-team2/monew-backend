package com.monew.monew_api.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class UserIdHeaderInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "MoNew-Request-User-ID";
    private static final String MDC_USER_ID_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader(USER_ID_HEADER);

        if (userId != null && !userId.isBlank()) {
            MDC.put(MDC_USER_ID_KEY, userId);
            log.info("[사용자 헤더 감지] {} 헤더 값: {}, URI: {}", USER_ID_HEADER, userId, request.getRequestURI());
        } else {
            log.info("[사용자 헤더 없음] {} 헤더가 요청에 포함되지 않음, URI: {}", USER_ID_HEADER, request.getRequestURI());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(MDC_USER_ID_KEY);
    }
}
