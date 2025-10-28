package com.monew.monew_api.common.exception.notification;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class NotificationAlreadyConfirmedException extends BaseException {

    public NotificationAlreadyConfirmedException() {
        super(ErrorCode.NOTIFICATION_ALREADY_CONFIRMED);
    }

    public NotificationAlreadyConfirmedException(Map<String, Object> details) {
        super(ErrorCode.NOTIFICATION_ALREADY_CONFIRMED, details);
    }
}
