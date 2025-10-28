package com.monew.monew_api.comments.dto;

import com.monew.monew_api.common.exception.comment.CommentInvalidArticleIdException;
import com.monew.monew_api.common.exception.comment.CommentInvalidUserIdException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRegisterRequest(
	@NotNull(message = "기사 ID는 필수입니다.")
	String articleId,

	@NotNull(message = "사용자 ID는 필수입니다.")
	String userId,

	@NotBlank(message = "댓글 내용을 입력해주세요.")
	@Size(max = 500, message = "댓글은 최대 500자까지 작성 가능합니다.")
	String content
) {

	public CommentRegisterRequest withUserId(String userId) {
		return new CommentRegisterRequest(this.articleId, userId, this.content);
	}

	public Long getArticleIdAsLong() {
		try {
			return Long.parseLong(articleId);
		} catch (NumberFormatException e) {
			throw new CommentInvalidArticleIdException(articleId);
		}
	}

	public Long getUserIdAsLong() {
		try {
			return Long.parseLong(userId);
		} catch (NumberFormatException e) {
			throw new CommentInvalidUserIdException(userId);
		}
	}
}
