package com.monew.monew_api.notification.service;

import com.monew.monew_api.common.dto.CursorPageResponse;
import com.monew.monew_api.notification.dto.request.NotificationCursorPageRequest;
import com.monew.monew_api.notification.dto.response.NotificationDto;
import com.monew.monew_api.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public CursorPageResponse<NotificationDto> getNonConfirmedNotifications(Long userId, NotificationCursorPageRequest cursorPageRequest) {
        return notificationRepository.findAllNonConfirmedNotifications(userId, cursorPageRequest);
    }
}