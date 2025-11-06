package com.monew.monew_api.common.config;

import com.monew.monew_api.common.interceptor.MDCLoggingInterceptor;
import com.monew.monew_api.common.interceptor.UserIdHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MDCLoggingInterceptor mdcLoggingInterceptor;
    private final UserIdHeaderInterceptor userIdHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcLoggingInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(userIdHeaderInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/users/login", "/api/users");
    }
}
