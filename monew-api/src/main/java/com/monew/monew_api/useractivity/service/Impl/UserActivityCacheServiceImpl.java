package com.monew.monew_api.useractivity.service.Impl;

import com.monew.monew_api.useractivity.event.CacheSaveEvent;
import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.mapper.UserActivityMapper;
import com.monew.monew_api.useractivity.repository.UserActivityCacheRepository;
import com.monew.monew_api.useractivity.service.UserActivityCacheService;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 캐시 기반 사용자 활동 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityCacheServiceImpl implements UserActivityCacheService {

    private final UserActivityCacheRepository cacheRepository;
    private final UserActivityService userActivityService;
    private final UserActivityMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 캐시 기반 사용자 활동 조회
     * 1. MongoDB 캐시 조회
     * 2. Cache Hit → 반환
     * 3. Cache Miss → PostgreSQL 조회 → 비동기 캐시 저장
     */
    @Transactional(readOnly = true)
    public UserActivityDto getUserActivityWithCache(String userId) {
        log.info("[UserActivityCache] 사용자 활동 조회 시작 (캐시): userId={}", userId);

        Optional<UserActivityCacheDocument> cached = cacheRepository.findById(userId);

        if (cached.isPresent()) {
            log.info("캐시 히트: userId={}", userId);
            return mapper.toDto(cached.get());
        }

        log.info("[UserActivityCache] 캐시 미스: userId={} - PostgreSQL 조회", userId);
        UserActivityDto result = userActivityService.getUserActivitySingleQuery(userId);

        try {
            eventPublisher.publishEvent(new CacheSaveEvent(userId, result));
            log.info("[UserActivityCache] 캐시 저장 이벤트 발행: userId={}", userId);
        } catch (Exception e) {
            log.error("[UserActivityCache] 캐시 저장 이벤트 발행 실패: userId={}", userId, e);
        }

        log.info("[UserActivityCache] 사용자 활동 조회 완료 (캐시): userId={}", userId);
        return result;
    }
}