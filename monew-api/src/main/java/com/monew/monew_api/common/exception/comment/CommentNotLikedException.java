package com.monew.monew_api.common.exception.comment;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentNotLikedException extends BaseException {
	public CommentNotLikedException() {
		super(ErrorCode.COMMENT_NOT_LIKED);
	}
}
