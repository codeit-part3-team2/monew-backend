package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.comments.event.CommentDeletedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 댓글 삭제 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentDeletedEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentDeletedEvent event) {
        log.info("[Listener] 댓글 삭제 이벤트 수신: commentId={}", event.commentId());

        try {
            cacheUpdateService.removeComment(event.commentId());
        } catch (Exception e) {
            log.error("[Listener] 댓글 삭제 캐시 처리 실패: commentId={}", event.commentId(), e);
        }
    }
}