package com.monew.monew_api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * 개발 환경용 PasswordEncoder - 평문 비밀번호 사용
     * 테스트 및 개발 편의성을 위해 비밀번호를 암호화하지 않음
     */
    @Bean
    @Profile("dev")
    public PasswordEncoder devPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    /**
     * 프로덕션 환경용 PasswordEncoder - BCrypt 암호화 사용
     * 실제 배포 환경에서 안전한 비밀번호 저장
     */
    @Bean
    @Profile("prod")
    public PasswordEncoder prodPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
