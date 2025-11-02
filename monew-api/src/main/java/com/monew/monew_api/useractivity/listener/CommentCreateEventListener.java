package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.comments.event.CommentCreatedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 댓글 작성 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentCreateEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentCreatedEvent event) {
        log.info("[Listener] 댓글 작성 이벤트 수신: commentId={}, userId={}, articleId={}",
                event.commentId(), event.userId(), event.articleId());

        try {
            // 1. 기사 댓글수 증가 (기존 조회한 사람들)
            cacheUpdateService.incrementArticleCommentCount(
                    event.articleId(),
                    event.getDelta()
            );

            // 2. 작성자 캐시에 댓글 추가 + 역인덱스 생성
            cacheUpdateService.addComment(
                    event.commentId(),
                    event.userId(),
                    event.userNickname(),
                    event.articleId(),
                    event.articleTitle(),
                    event.content(),
                    event.likeCount(),
                    event.createdAt()
            );

            log.info("[Listener] 댓글 작성 캐시 업데이트 완료: commentId={}", event.commentId());

        } catch (Exception e) {
            log.error("[Listener] 댓글 작성 캐시 업데이트 실패: commentId={}", event.commentId(), e);
        }
    }
}