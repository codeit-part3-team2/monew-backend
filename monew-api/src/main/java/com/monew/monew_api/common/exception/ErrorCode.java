package com.monew.monew_api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 사용자 - USER
    USER_EMAIL_DUPLICATED(HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "이메일 또는 비밀번호가 일치하지 않습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "수정 또는 삭제 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자 정보를 찾을 수 없습니다."),

    // 관심사 - INTEREST
    INTEREST_DUPLICATED(HttpStatus.CONFLICT.value(), "유사한 관심사가 이미 존재합니다."),
    INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "관심사 정보를 찾을 수 없습니다."),

    // 뉴스 기사 - ARTICLE
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "뉴스 기사 정보를 찾을 수 없습니다."),

    // 댓글 - COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "댓글 정보를 찾을 수 없습니다."),

    // 알림 - NOTIFICATION
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "알림 정보를 찾을 수 없습니다.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
