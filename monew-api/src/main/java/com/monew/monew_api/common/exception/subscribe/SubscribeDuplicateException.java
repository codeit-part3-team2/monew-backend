package com.monew.monew_api.common.exception.subscribe;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;
import java.util.Map;

public class SubscribeDuplicateException extends BaseException {

  public SubscribeDuplicateException() {
    super(ErrorCode.SUBSCRIBE_DUPLICATE);
  }

  public SubscribeDuplicateException(Map<String, Object> details) {
    super(ErrorCode.SUBSCRIBE_DUPLICATE, details);
  }
}
