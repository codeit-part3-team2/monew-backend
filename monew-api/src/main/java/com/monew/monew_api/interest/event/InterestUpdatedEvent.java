package com.monew.monew_api.interest.event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interest 정보 변경 이벤트
 * Interest 정보를 수정했을 때 발행
 * @param interestId
 * @param newKeywords
 * @param occurredAt
 */
public record InterestUpdatedEvent(
        Long interestId,
        List<String> newKeywords,
        LocalDateTime occurredAt
) {
    public static InterestUpdatedEvent of(Long interestId, List<String> newKeywords) {
        return new InterestUpdatedEvent(interestId, newKeywords, LocalDateTime.now());
    }
}