package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.comments.event.CommentUnlikedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 댓글 좋아요 취소 이벤트 리스너
 * 사용자가 댓글 좋아요를 취소했을 때 캐시 업데이트 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentUnlikedEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentUnlikedEvent e) {
        cacheUpdateService.updateCommentLikeCount(e.commentId(), -1);
        cacheUpdateService.removeCommentLike(e.likedByUserId(), e.commentId());
        log.info("[Listener] CommentUnlikedEvent handled: commentId={}, likedBy={}",
                e.commentId(), e.likedByUserId());
    }
}
