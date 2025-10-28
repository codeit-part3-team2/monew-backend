package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;

public record CommentActivityDto(
	String id,
	String articleId,
	String articleTitle,
	String userId,
	String userNickname,
	String content,
	int likeCount,
	LocalDateTime createdAt
) {
}
