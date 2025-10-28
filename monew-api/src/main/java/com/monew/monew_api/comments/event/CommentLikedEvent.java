package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

public record CommentLikedEvent(Long commentId,
								Long commentAuthorId,
								Long likedByUserId,
								LocalDateTime createdAt) {
}
