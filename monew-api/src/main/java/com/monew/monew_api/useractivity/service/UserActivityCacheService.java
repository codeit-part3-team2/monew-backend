package com.monew.monew_api.useractivity.service;

import com.monew.monew_api.useractivity.dto.UserActivityDto;

public interface UserActivityCacheService {
    /**
     * 사용자 활동내역 조회 (캐시 적용)
     * MongoDB 캐시 확인 → 없으면 PostgreSQL 조회 → 캐시 저장
     */
    UserActivityDto getUserActivityWithCache(String userId);
}
