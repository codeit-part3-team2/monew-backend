package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

/**
 * 댓글 작성 이벤트
 * 사용자가 기사에 댓글을 작성했을 때 발행
 * 기사의 commentCount +1
 * Update 전략 사용
 * @param commentId
 * @param articleId
 * @param articleTitle
 * @param userId
 * @param userNickname
 * @param content
 * @param likeCount
 * @param createdAt
 * @param occurredAt
 */
public record CommentCreatedEvent(
        Long commentId,
        Long articleId,
        String articleTitle,
        Long userId,
        String userNickname,
        String content,
        Integer likeCount,
        LocalDateTime createdAt,
        LocalDateTime occurredAt
) {

    public static CommentCreatedEvent of(
            Long commentId,
            Long articleId,
            String articleTitle,
            Long userId,
            String userNickname,
            String content,
            Integer likeCount,
            LocalDateTime createdAt
    ) {
        return new CommentCreatedEvent(
                commentId,
                articleId,
                articleTitle,
                userId,
                userNickname,
                content,
                likeCount,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Integer getDelta() {
        return 1;
    }
}