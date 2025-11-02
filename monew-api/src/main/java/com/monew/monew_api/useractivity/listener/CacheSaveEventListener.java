package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.useractivity.event.CacheSaveEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 캐시 저장 이벤트 리스너
 * PostgreSQL 조회 후 MongoDB에 비동기 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheSaveEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CacheSaveEvent event) {
        log.info("[Listener] 캐시 저장 이벤트 수신: userId={}", event.userId());

        try {
            cacheUpdateService.saveCache(
                    event.userId(),
                    event.data()
            );
        } catch (Exception e) {
            log.error("[Listener] 캐시 저장 실패: userId={}", event.userId(), e);
        }
    }
}