package com.monew.monew_api.comments.dto;

import com.monew.monew_api.comments.entity.Comment;

public record CommentResponseDto(
	String id,
	String articleId,
	String userId,
	String content,
	int likeCount,
	String createdAt,
	String updatedAt
) {

	public static CommentResponseDto from(Comment comment) {
		return new CommentResponseDto(
			String.valueOf(comment.getId()),
			String.valueOf(comment.getArticleId()),
			String.valueOf(comment.getUserId()),
			comment.getContent(),
			comment.getLikeCount(),
			comment.getCreatedAt().toString(),
			comment.getUpdatedAt().toString()
		);
	}

}
