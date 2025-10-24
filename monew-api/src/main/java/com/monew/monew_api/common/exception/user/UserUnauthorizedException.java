package com.monew.monew_api.common.exception.user;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class UserUnauthorizedException extends BaseException {

  public UserUnauthorizedException() {
    super(ErrorCode.USER_UNAUTHORIZED);
  }

  public UserUnauthorizedException(Map<String, Object> details) {
    super(ErrorCode.USER_UNAUTHORIZED, details);
  }
}
