package com.monew.monew_api.common.exception.notification;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class NotificationAccessDeniedException extends BaseException {

    public NotificationAccessDeniedException() {
        super(ErrorCode.NOTIFICATION_ACCESS_DENIED);
    }

    public NotificationAccessDeniedException(Map<String, Object> details) {
        super(ErrorCode.NOTIFICATION_ACCESS_DENIED, details);
    }
}