package com.monew.monew_api.common.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class BaseException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public BaseException(ErrorCode errorCode) {
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Map.of();
    }

    public BaseException(ErrorCode errorCode, Map<String, Object> details) {
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}
