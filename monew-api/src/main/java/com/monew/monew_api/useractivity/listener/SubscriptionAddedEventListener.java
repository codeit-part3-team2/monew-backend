package com.monew.monew_api.useractivity.listener;

import com.monew.monew_api.useractivity.service.CacheUpdateService;
import com.monew.monew_api.subscribe.event.SubscriptionAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionAddedEventListener {

    private final CacheUpdateService cacheUpdateService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SubscriptionAddedEvent e) {
        cacheUpdateService.addSubscription(
                e.userId(),
                e.subscriptionId(),
                e.interestId(),
                e.interestName(),
                e.interestKeywords(),
                e.interestSubscriberCount(),
                e.createdAt()
        );
        log.info("[Listener] SubscriptionAdded handled: userId={}, subId={}, interestId={}",
                e.userId(), e.subscriptionId(), e.interestId());
    }
}
