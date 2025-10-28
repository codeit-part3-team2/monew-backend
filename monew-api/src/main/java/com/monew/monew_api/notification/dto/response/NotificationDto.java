package com.monew.monew_api.notification.dto.response;

import com.monew.monew_api.notification.enums.ResourceType;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean confirmed,
        Long userId,
        String content,
        ResourceType resourceType,
        Long resourceId
) {
}
