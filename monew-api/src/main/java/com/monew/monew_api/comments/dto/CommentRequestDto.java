package com.monew.monew_api.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

	@NotNull(message = "articleId는 필수입니다.")
	private String articleId;

	@NotBlank(message = "댓글 내용을 작성해주세요.")
	@Size(max = 500, message = "댓글은 최대 500자까지 작성 가능합니다.")
	private String content;

	public Long getArticleIdAsLong() {
		try {
			return Long.parseLong(articleId);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("잘못된 ID 형식입니다: " + articleId);
		}
	}

}
