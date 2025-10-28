package com.monew.monew_api.common.exception.article;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

import java.util.Map;

public class ArticleAlreadyViewedException extends BaseException {

  public ArticleAlreadyViewedException() {
    super(ErrorCode.ARTICLE_ALREADY_VIEWED);
  }

  public ArticleAlreadyViewedException(Map<String, Object> details) {
    super(ErrorCode.ARTICLE_ALREADY_VIEWED, details);
  }
}
