package com.monew.monew_api.comments.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record CursorPageResponseCommentDto(
	List<CommentDto> content,
	String nextCursor,
	ZonedDateTime nextAfter,
	int size,
	long totalElements,
	boolean hasNext
) {
}
