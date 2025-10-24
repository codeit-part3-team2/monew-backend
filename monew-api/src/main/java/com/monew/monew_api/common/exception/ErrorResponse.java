package com.monew.monew_api.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private final Instant timestamp;
    private final String code;
    private final String message;
    private final Map<String, Object> details;
    private final String exceptionType;
    private final int status;

    public static ErrorResponse of(BaseException e, String path) {
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> mergedDetails = new HashMap<>(e.getDetails());
        mergedDetails.put("path", path);

        return ErrorResponse.builder()
                .timestamp(e.getTimestamp())
                .status(errorCode.getStatus())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .details(mergedDetails)
                .exceptionType(e.getClass().getSimpleName())
                .build();
    }
}