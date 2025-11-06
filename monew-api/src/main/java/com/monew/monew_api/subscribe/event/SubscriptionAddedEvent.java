package com.monew.monew_api.subscribe.event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구독 추가 이벤트
 * @param userId
 * @param subscriptionId
 * @param interestId
 * @param interestName
 * @param interestKeywords
 * @param interestSubscriberCount
 * @param createdAt
 * @param occurredAt
 */
public record SubscriptionAddedEvent(
        Long userId,
        Long subscriptionId,
        Long interestId,
        String interestName,
        List<String> interestKeywords,
        Integer interestSubscriberCount,
        LocalDateTime createdAt,
        LocalDateTime occurredAt
) {
    public static SubscriptionAddedEvent of(
            Long userId, Long subscriptionId, Long interestId, String interestName,
            List<String> interestKeywords, Integer interestSubscriberCount, LocalDateTime createdAt
    ) {
        return new SubscriptionAddedEvent(
                userId, subscriptionId, interestId, interestName, interestKeywords,
                interestSubscriberCount, createdAt, LocalDateTime.now()
        );
    }
}