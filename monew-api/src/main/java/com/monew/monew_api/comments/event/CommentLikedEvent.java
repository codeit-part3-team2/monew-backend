package com.monew.monew_api.comments.event;

public record CommentLikedEvent(Long commentId,
								Long commentAuthorId,
								String likerNickname) {
}
