package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.useractivity.service.CacheUpdateService;
import com.monew.monew_api.subscribe.event.SubscriptionRemovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionRemovedEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SubscriptionRemovedEvent e) {
        cacheUpdateService.removeSubscription(e.userId(), e.subscriptionId(), e.interestId());
        log.info("[Listener] SubscriptionRemoved handled: userId={}, subId={}",
                e.userId(), e.subscriptionId());
    }
}
