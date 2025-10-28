package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;

public record CommentLikeDto(
	String id,
	String likedBy,
	LocalDateTime createdAt,
	String commentId,
	String articleId,
	String commentUserId,
	String commentUserNickname,
	String commentContent,
	int commentLikeCount,
	LocalDateTime commentCreatedAt
) {
}
