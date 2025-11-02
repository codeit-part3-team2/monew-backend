package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.article.event.ArticleViewedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 기사 조회 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleViewEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ArticleViewedEvent event) {
        log.info("[Listener] 기사 조회 이벤트 수신: articleId={}, userId={}",
                event.articleId(), event.userId());

        try {
            // 1. 조회수 증가 (기존 조회한 사람들)
            cacheUpdateService.incrementArticleViewCount(
                    event.articleId(),
                    event.getDelta()
            );

            // 2. 조회한 사람 캐시에 추가 + 역인덱스 생성
            cacheUpdateService.addArticleView(
                    event.viewId(),
                    event.userId(),
                    event.createdAt(),
                    event.articleId(),
                    event.source(),
                    event.sourceUrl(),
                    event.articleTitle(),
                    event.articlePublishedDate(),
                    event.articleSummary(),
                    event.articleCommentCount(),
                    event.articleViewCount()
            );

            log.info("[Listener] 기사 조회 캐시 업데이트 완료: articleId={}", event.articleId());

        } catch (Exception e) {
            log.error("[Listener] 기사 조회 캐시 업데이트 실패: articleId={}", event.articleId(), e);
        }
    }
}