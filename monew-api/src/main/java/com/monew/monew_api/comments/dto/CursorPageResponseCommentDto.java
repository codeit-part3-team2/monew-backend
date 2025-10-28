package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseCommentDto(
	List<CommentDto> content,
	String nextCursor,
	LocalDateTime nextAfter,
	int size,
	long totalElements,
	boolean hasNext
) {
}
