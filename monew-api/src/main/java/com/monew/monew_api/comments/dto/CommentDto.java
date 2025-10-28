package com.monew.monew_api.comments.dto;

import com.monew.monew_api.comments.entity.Comment;

public record CommentDto(
	String id,
	String articleId,
	String userId,
	String userNickname,
	String content,
	int likeCount,
	boolean likedByMe,
	boolean isMyComment,
	String createdAt
) {

	public static CommentDto from(Comment comment, boolean likedByMe) {
		return new CommentDto(
			String.valueOf(comment.getId()),
			String.valueOf(comment.getArticle().getId()),
			String.valueOf(comment.getUser().getId()),
			comment.getUser().getNickname(),
			comment.getContent(),
			comment.getLikeCount(),
			likedByMe,
			false,
			comment.getCreatedAt().toString()
		);
	}

	public static CommentDto from(Comment comment, boolean likedByMe, Long requestUserId) {
		boolean isMyComment = comment.getUser().getId().equals(requestUserId);

		return new CommentDto(
			String.valueOf(comment.getId()),
			String.valueOf(comment.getArticle().getId()),
			String.valueOf(comment.getUser().getId()),
			comment.getUser().getNickname(),
			comment.getContent(),
			comment.getLikeCount(),
			likedByMe,
			isMyComment,
			comment.getCreatedAt().toString()
		);
	}
}
