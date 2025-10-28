package com.monew.monew_api.comments.dto;

import com.monew.monew_api.comments.entity.CommentLike;

public record CommentLikeActivityDto(
	String id,
	String commentId,
	String articleId,
	String articleTitle,
	String commentUserId,
	String commentUserNickname,
	String commentContent,
	int commentLikeCount,
	String commentCreatedAt,
	String createdAt
) {

	public static CommentLikeActivityDto from(CommentLike like) {
		return new CommentLikeActivityDto(
			String.valueOf(like.getId()),
			String.valueOf(like.getComment().getId()),
			String.valueOf(like.getComment().getArticleId()),
			like.getComment().getArticle().getTitle(),
			String.valueOf(like.getComment().getUserId()),
			like.getComment().getUser().getNickname(),
			like.getComment().getContent(),
			like.getComment().getLikeCount(),
			like.getComment().getCreatedAt().toString(),
			like.getCreatedAt().toString()
		);
	}

}
