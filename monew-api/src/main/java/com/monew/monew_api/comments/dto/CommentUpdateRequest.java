package com.monew.monew_api.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
	@NotBlank(message = "댓글 내용을 입력해주세요.")
	@Size(max = 500, message = "댓글은 최대 500자까지 작성 가능합니다.")
	String content
) {
}
