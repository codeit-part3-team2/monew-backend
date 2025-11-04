package com.monew.monew_api.useractivity.event;

import com.monew.monew_api.useractivity.dto.UserActivityDto;

/**
 * 캐시 저장 이벤트
 * PostgreSQL 조회 후 MongoDB에 비동기 캐시 저장
 * useractivity 내부, create 전략
 * @param userId
 * @param data
 */
public record CacheSaveEvent(
        String userId,
        UserActivityDto data
) {
}