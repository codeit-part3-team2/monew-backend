package com.monew.monew_api.common.exception.comment;

import com.monew.monew_api.common.exception.BaseException;
import com.monew.monew_api.common.exception.ErrorCode;

public class CommentAlreadyLikedException extends BaseException {
  public CommentAlreadyLikedException() {
    super(ErrorCode.COMMENT_ALREADY_LIKED);
  }
}
