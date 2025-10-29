package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;

import com.monew.monew_api.comments.entity.CommentLike;

public record CommentLikeDto(
	Long id,
	Long commentId,
	Long articleId,
	Long likedBy,
	Long commentUserId,
	String commentUserNickname,
	String commentContent,
	int commentLikeCount,
	String commentCreatedAt,
	String createdAt
) {

	public static CommentLikeDto from(CommentLike like) {
		return new CommentLikeDto(
			like.getId(),
			like.getComment().getId(),
			like.getComment().getArticleId(),
			like.getUser().getId(),
			like.getComment().getUserId(),
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
			commentId,
			null,
			userId,
			null, null, null,
			-1,
			null,
			LocalDateTime.now().toString()
		);
	}

}
