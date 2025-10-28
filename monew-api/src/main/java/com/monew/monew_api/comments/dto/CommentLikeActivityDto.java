package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;

public record CommentLikeActivityDto(
	String id,
	LocalDateTime createdAt,
	String commentId,
	String articleId,
	String articleTitle,
	String commentUserId,
	String commentUserNickname,
	String commentContent,
	int commentLikeCount,
	LocalDateTime commentCreatedAt
) {}
