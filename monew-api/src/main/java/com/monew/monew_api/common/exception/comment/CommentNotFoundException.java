package com.monew.monew_api.common.exception.comment;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class CommentNotFoundException extends BaseException {

    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }

    public CommentNotFoundException(Map<String, Object> details) {
        super(ErrorCode.COMMENT_NOT_FOUND, details);
    }
}
