package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;

import com.monew.monew_api.comments.entity.CommentLike;

public record CommentLikeDto(
	String id,
	String commentId,
	String articleId,
	String likedBy,
	String commentUserId,
	String commentUserNickname,
	String commentContent,
	int commentLikeCount,
	String commentCreatedAt,
	String createdAt
) {

	public static CommentLikeDto from(CommentLike like) {
		return new CommentLikeDto(
			String.valueOf(like.getId()),
			String.valueOf(like.getComment().getId()),
			String.valueOf(like.getComment().getArticleId()),
			String.valueOf(like.getUser().getId()),
			String.valueOf(like.getComment().getUserId()),
			like.getComment().getUser().getNickname(),
			like.getComment().getContent(),
			like.getComment().getLikeCount(),
			like.getComment().getCreatedAt().toString(),
			like.getCreatedAt().toString()
		);
	}

	public static CommentLikeDto of(Long commentId, Long userId) {
		return new CommentLikeDto(
			null,
			String.valueOf(commentId),
			null,
			String.valueOf(userId),
			null, null, null,
			-1,
			null,
			LocalDateTime.now().toString()
		);
	}

}
