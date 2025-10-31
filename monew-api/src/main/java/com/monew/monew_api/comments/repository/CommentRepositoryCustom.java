package com.monew.monew_api.comments.repository;

import java.time.LocalDateTime;

import com.monew.monew_api.comments.dto.CursorPageResponseCommentDto;

public interface CommentRepositoryCustom {

	// 댓글 조회
	CursorPageResponseCommentDto searchComments(
		Long articleId,
		String orderBy,
		String cursor,
		LocalDateTime after,
		int limit,
		Long userId
	);
}
