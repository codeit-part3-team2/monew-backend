package com.monew.monew_api.common.exception.user;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class UserForbiddenException extends BaseException {

  public UserForbiddenException() {
    super(ErrorCode.USER_FORBIDDEN);
  }

  public UserForbiddenException(Map<String, Object> details) {
    super(ErrorCode.USER_FORBIDDEN, details);
  }
}
