package com.monew.monew_api.common.exception.comment;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentUserNotFoundException extends BaseException {
	public CommentUserNotFoundException() {
		super(ErrorCode.COMMENT_USER_NOT_FOUND);
	}
}
