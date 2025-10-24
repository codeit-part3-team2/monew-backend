package com.monew.monew_api.common.exception.interest;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class InterestNotFoundException extends BaseException {

    public InterestNotFoundException() {
        super(ErrorCode.INTEREST_NOT_FOUND);
    }

    public InterestNotFoundException(Map<String, Object> details) {
        super(ErrorCode.INTEREST_NOT_FOUND, details);
    }
}
