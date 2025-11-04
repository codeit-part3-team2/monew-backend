package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

/**
 * 댓글 삭제 이벤트
 * 댓글이 삭제되었을 때 발행
 * @param commentId
 * @param occurredAt
 */
public record CommentDeletedEvent(
        Long commentId,
        LocalDateTime occurredAt
) {
    public static CommentDeletedEvent of(Long commentId) {
        return new CommentDeletedEvent(commentId, LocalDateTime.now());
    }
}