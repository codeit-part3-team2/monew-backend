package com.monew.monew_api.common.exception.notification;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class NotificationNotFoundException extends BaseException {

  public NotificationNotFoundException() {
    super(ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public NotificationNotFoundException(Map<String, Object> details) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND, details);
  }
}
