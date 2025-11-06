package com.monew.monew_api.subscribe.event;

import java.time.LocalDateTime;

/**
 * 구독 제거 이벤트
 * @param userId
 * @param subscriptionId
 * @param occurredAt
 */
public record SubscriptionRemovedEvent(
        Long userId,
        Long subscriptionId,
        Long interestId,
        LocalDateTime occurredAt
) {
    public static SubscriptionRemovedEvent of(Long userId, Long subscriptionId, Long interestId) {
        return new SubscriptionRemovedEvent(userId, subscriptionId, interestId, LocalDateTime.now());
    }
}