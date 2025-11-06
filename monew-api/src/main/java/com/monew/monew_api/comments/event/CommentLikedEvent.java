package com.monew.monew_api.comments.event;

import java.time.LocalDateTime;

/**
 * 댓글 좋아요/취소 이벤트
 * 사용자가 댓글에 좋아요를 누르거나 취소했을 때 발행
 * Update 전략 사용
 * @param likeId
 * @param likeCreatedAt
 * @param commentId
 * @param articleId
 * @param articleTitle
 * @param commentAuthorId
 * @param commentUserNickname
 * @param commentContent
 * @param commentLikeCount
 * @param commentCreatedAt
 * @param likedByUserId
 * @param likerNickname
 * @param occurredAt
 */
public record CommentLikedEvent(
        Long likeId,
        LocalDateTime likeCreatedAt,
        Long commentId,
        Long articleId,
        String articleTitle,
        Long commentAuthorId,
        String commentUserNickname,
        String commentContent,
        Integer commentLikeCount,
        LocalDateTime commentCreatedAt,
        Long likedByUserId,
        String likerNickname,
        LocalDateTime occurredAt
) {
    public static CommentLikedEvent of(
            Long likeId,
            LocalDateTime likeCreatedAt,
            Long commentId,
            Long articleId,
            String articleTitle,
            Long commentAuthorId,
            String commentUserNickname,
            String commentContent,
            Integer commentLikeCount,
            LocalDateTime commentCreatedAt,
            Long likedByUserId,
            String likerNickname
    ) {
        return new CommentLikedEvent(
                likeId, likeCreatedAt, commentId, articleId, articleTitle,
                commentAuthorId, commentUserNickname, commentContent,
                commentLikeCount, commentCreatedAt, likedByUserId, likerNickname,
                LocalDateTime.now()
        );
    }
}