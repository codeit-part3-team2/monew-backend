package com.monew.monew_api.common.exception.interest;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class InterestDuplicatedException extends BaseException {

    public InterestDuplicatedException() {
        super(ErrorCode.INTEREST_DUPLICATED);
    }

    public InterestDuplicatedException(Map<String, Object> details) {
        super(ErrorCode.INTEREST_DUPLICATED, details);
    }
}
