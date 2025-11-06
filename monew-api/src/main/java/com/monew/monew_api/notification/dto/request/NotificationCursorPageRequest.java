package com.monew.monew_api.notification.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record NotificationCursorPageRequest(
        String cursor,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime after,

        @NotNull(message = "조회할 개수는 필수값입니다.")
        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 50, message = "조회 개수는 50을 초과할 수 없습니다.")
        Integer limit
) {
}
