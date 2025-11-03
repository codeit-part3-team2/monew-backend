package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

public record CommentCreatedEvent(Long commentId,
								  Long userId,
								  Long articleId,
								  LocalDateTime createdAt) {
}
