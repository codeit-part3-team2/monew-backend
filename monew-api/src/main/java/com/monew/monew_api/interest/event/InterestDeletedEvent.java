package com.monew.monew_api.interest.event;

import java.time.LocalDateTime;

/**
 * Interest 삭제 이벤트
 * @param interestId
 * @param occurredAt
 */
public record InterestDeletedEvent(
        Long interestId,
        LocalDateTime occurredAt
) {
    public static InterestDeletedEvent of(Long interestId) {
        return new InterestDeletedEvent(interestId, LocalDateTime.now());
    }
}
