package com.monew.monew_api.common.exception.subscribe;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;
import java.util.Map;

public class SubscribeNotFoundException extends BaseException {

  public SubscribeNotFoundException() {
    super(ErrorCode.SUBSCRIBE_NOT_FOUND);
  }

  public SubscribeNotFoundException(Map<String, Object> details) {
    super(ErrorCode.SUBSCRIBE_NOT_FOUND, details);
  }

}
