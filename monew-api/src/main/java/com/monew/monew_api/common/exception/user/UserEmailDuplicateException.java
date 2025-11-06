package com.monew.monew_api.common.exception.user;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class UserEmailDuplicateException extends BaseException {

  public UserEmailDuplicateException() {
    super(ErrorCode.USER_EMAIL_DUPLICATED);
  }

  public UserEmailDuplicateException(Map<String, Object> details) {
    super(ErrorCode.USER_EMAIL_DUPLICATED, details);
  }
}
