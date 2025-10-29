package com.monew.monew_api.notification.repository;

import com.monew.monew_api.common.dto.CursorPageResponse;
import com.monew.monew_api.notification.dto.request.NotificationCursorPageRequest;
import com.monew.monew_api.notification.dto.response.NotificationDto;

public interface NotificationRepositoryCustom {
    CursorPageResponse<NotificationDto> findAllNonConfirmedNotifications(Long id, NotificationCursorPageRequest cursorPageRequest);
}
