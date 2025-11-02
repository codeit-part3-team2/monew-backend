package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.interest.event.InterestUpdatedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Interest 정보 변경 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterestUpdateEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InterestUpdatedEvent event) {
        log.info("[Listener] Interest 정보 변경 이벤트 수신: interestId={}",
                event.interestId());

        try {
            cacheUpdateService.updateInterestKeyword(
                    event.interestId(),
                    event.newKeywords()
            );
        } catch (Exception e) {
            log.error("[Listener] Interest 정보 캐시 업데이트 실패: interestId={}", event.interestId(), e);
        }
    }
}