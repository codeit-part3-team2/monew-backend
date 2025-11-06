package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

/**
 * 댓글 좋아요 취소 이벤트
 * 사용자가 댓글 좋아요를 취소했을 때 발행
 * @param commentId
 * @param likedByUserId
 * @param occurredAt
 */
public record CommentUnlikedEvent(
        Long commentId,
        Long likedByUserId,
        LocalDateTime occurredAt
) {
    public static CommentUnlikedEvent of(Long commentId, Long likedByUserId) {
        return new CommentUnlikedEvent(commentId, likedByUserId, LocalDateTime.now());
    }
}
