package com.monew.monew_api.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e, HttpServletRequest request) {
        log.warn("[비즈니스 예외 발생] 코드: {}, 메시지: {}, 요청 URI: {}",
                e.getErrorCode().name(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("[입력값 검증 실패] 요청 URI: {}, 에러 필드 수: {}, 상세: {}",
                request.getRequestURI(),
                fieldErrors.size(),
                fieldErrors);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(400)
                .code("VALIDATION_ERROR")
                .message("요청 데이터의 유효성 검증에 실패했습니다.")
                .details(Map.of(
                        "path", request.getRequestURI(),
                        "errors", fieldErrors
                ))
                .exceptionType(e.getClass().getSimpleName())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("[서버 내부 오류] 예외 타입: {}, 메시지: {}, URI: {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                request.getRequestURI(),
                e);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(500)
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .details(Map.of("path", request.getRequestURI()))
                .exceptionType(e.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(500).body(response);
    }
}
