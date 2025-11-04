package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.interest.event.InterestDeletedEvent;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestDeletedEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InterestDeletedEvent e) {
        cacheUpdateService.removeInterest(e.interestId());
        log.info("[Listener] InterestDeleted handled: interestId={}", e.interestId());
    }
}
