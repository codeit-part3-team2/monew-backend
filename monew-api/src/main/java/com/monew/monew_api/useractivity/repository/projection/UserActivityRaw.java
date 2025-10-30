package com.monew.monew_api.useractivity.repository.projection;

import java.time.LocalDateTime;

/**
 * 사용자 활동 네이티브 쿼리 결과를 담는 불변 데이터 컨테이너
 * alias와 일치하는 필드명을 사용
 * jsonb 필드는 String으로 들어오기 때문에 Mapper에서 List 변환 필요
 */
public record UserActivityRaw(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt,
        String subscriptions,
        String comments,
        String likes,
        String views
) {}