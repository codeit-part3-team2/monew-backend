package com.monew.monew_api.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRegisterRequest(
	@NotNull(message = "기사 ID는 필수입니다.")
	String articleId,

	@NotNull(message = "사용자 ID는 필수입니다.")
	String userId,

	@NotBlank(message = "댓글 내용을 작성해주세요.")
	@Size(max = 500, message = "댓글은 최대 500자까지 작성 가능합니다.")
	String content
) {

	public Long getArticleIdAsLong() {
		try {
			return Long.parseLong(articleId);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("잘못된 기사 ID 형식입니다.");
		}
	}

	public Long getUserIdAsLong() {
		try {
			return Long.parseLong(userId);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("잘못된 사용자 ID 형식입니다.");
		}
	}

}
