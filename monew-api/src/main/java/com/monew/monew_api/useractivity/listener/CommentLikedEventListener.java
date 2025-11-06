package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 댓글 좋아요 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentLikedEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentLikedEvent e) {
        cacheUpdateService.addCommentLike(
                e.likeId(),
                e.likedByUserId(),
                e.likeCreatedAt(),
                e.commentId(),
                e.articleId(),
                e.articleTitle(),
                e.commentAuthorId(),
                e.commentUserNickname(),
                e.commentContent(),
                e.commentLikeCount(),
                e.commentCreatedAt()
        );
        cacheUpdateService.updateCommentLikeCount(e.commentId(), +1);

        log.info("[Listener] CommentLikedEvent handled: commentId={}, likedBy={}",
                e.commentId(), e.likedByUserId());
    }
}