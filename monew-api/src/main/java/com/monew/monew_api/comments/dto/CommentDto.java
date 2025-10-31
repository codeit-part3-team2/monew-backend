package com.monew.monew_api.comments.dto;

import com.monew.monew_api.comments.entity.Comment;
import com.querydsl.core.annotations.QueryProjection;

public record CommentDto(
	Long id,
	Long userId,
	Long articleId,
	String userNickname,
	String content,
	int likeCount,
	boolean likedByMe,
	String createdAt
) {
	@QueryProjection  // QCommentDto를 생성
	public CommentDto {
	}
	public static CommentDto from(Comment comment, boolean likedByMe) {
		return new CommentDto(
			comment.getId(),
			comment.getUser().getId(),
			comment.getArticle().getId(),
			comment.getUser().getNickname(),
			comment.getContent(),
			comment.getLikeCount(),
			likedByMe,
			comment.getCreatedAt().toString()
		);
	}

}
