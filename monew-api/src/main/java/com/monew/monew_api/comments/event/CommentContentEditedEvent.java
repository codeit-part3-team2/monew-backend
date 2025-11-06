package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

public record CommentContentEditedEvent(
        Long commentId,
        String newContent,
        LocalDateTime occurredAt
) {
    public static CommentContentEditedEvent of(Long commentId, String newContent) {
        return new CommentContentEditedEvent(commentId, newContent, LocalDateTime.now());
    }
}