package com.monew.monew_api.common.exception.comment;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentArticleNotFoundException extends BaseException {
	public CommentArticleNotFoundException() {
		super(ErrorCode.COMMENT_ARTICLE_NOT_FOUND);
	}
}
