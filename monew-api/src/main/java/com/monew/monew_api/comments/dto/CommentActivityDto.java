package com.monew.monew_api.comments.dto;

import com.monew.monew_api.comments.entity.Comment;

public record CommentActivityDto(
	String id,
	String articleId,
	String articleTitle,
	String userId,
	String userNickname,
	String content,
	int likeCount,
	String createdAt
) {

	public static CommentActivityDto from(Comment comment) {
		return new CommentActivityDto(
			String.valueOf(comment.getId()),
			String.valueOf(comment.getArticle().getId()),
			comment.getArticle().getTitle(),
			String.valueOf(comment.getUser().getId()),
			comment.getUser().getNickname(),
			comment.getContent(),
			comment.getLikeCount(),
			comment.getCreatedAt().toString()
		);
	}

}
