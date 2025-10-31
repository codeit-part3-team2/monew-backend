package com.monew.monew_api.common.exception.comment;

import java.util.Map;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentInvalidUserIdException extends BaseException {

	public CommentInvalidUserIdException(String invalidValue) {
		super(ErrorCode.COMMENT_INVALID_USER_ID, Map.of("userId", invalidValue));
	}

}
