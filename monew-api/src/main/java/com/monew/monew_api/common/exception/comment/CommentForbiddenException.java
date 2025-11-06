package com.monew.monew_api.common.exception.comment;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentForbiddenException extends BaseException {
	public CommentForbiddenException() {
		super(ErrorCode.COMMENT_FORBIDDEN);
	}

}
