package com.monew.monew_api.comments.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.monew.monew_api.comments.entity.Comment;

public interface CommentRepositoryCustom {

	List<Comment> findPageByArticleIdOrderByCreatedAtDesc(
		Long articleId,
		Long cursorId,
		LocalDateTime cursorCreatedAt,
		int limit
	);

	List<Comment> findPageByArticleIdOrderByLikeCountDesc(
		Long articleId,
		Long cursorId,
		Integer cursorLikeCount,
		int limit
	);

}
