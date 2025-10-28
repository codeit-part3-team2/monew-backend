package com.monew.monew_api.notification.controller;

import com.monew.monew_api.common.dto.CursorPageResponse;
import com.monew.monew_api.notification.dto.request.NotificationCursorPageRequest;
import com.monew.monew_api.notification.dto.response.NotificationDto;
import com.monew.monew_api.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final String REQUEST_HEADER_USER_ID = "MoNew-Request-User-ID";
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CursorPageResponse<NotificationDto>> getNotifications(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                                                                @ModelAttribute @Valid NotificationCursorPageRequest cursorPageRequest) {
        log.info("[API 요청] GET /api/notifications - 전체 조회, 사용자 ID: {}", userId);
        CursorPageResponse<NotificationDto> notifications = notificationService.getNonConfirmedNotifications(userId, cursorPageRequest);
        log.info("[API 응답] GET /api/notifications - 조회 기록 성공, 사용자 ID: {}, 알림 개수: {}", userId, notifications.size());

        return ResponseEntity.ok(notifications);
    }
}
