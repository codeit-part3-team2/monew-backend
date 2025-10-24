package com.monew.monew_api.common.exception.article;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class ArticleNotFoundException extends BaseException {

    public ArticleNotFoundException() {
        super(ErrorCode.ARTICLE_NOT_FOUND);
    }

    public ArticleNotFoundException(Map<String, Object> details) {
        super(ErrorCode.ARTICLE_NOT_FOUND, details);
    }
}
