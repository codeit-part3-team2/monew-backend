package com.monew.monew_api.common.exception.comment;

import java.util.Map;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentInvalidArticleIdException extends BaseException {
	public CommentInvalidArticleIdException(String invalidValue) {
		super(ErrorCode.COMMENT_INVALID_ARTICLE_ID, Map.of("articleId", invalidValue));
	}
}
