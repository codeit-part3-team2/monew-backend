package com.monew.monew_batch.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAsyncService {

    private final RestTemplate restTemplate;

    @Value("${monew.api.url}")
    private String monewApiUrl;

    @Async
    public void sendNotification(Map<Long, Integer> stats) {
        if (stats == null || stats.isEmpty()) {
            log.info("전송할 알림 데이터 없음");
            return;
        }

        try {
            String apiUrl = monewApiUrl + "/api/internal/notifications/articles-registered";
            restTemplate.postForEntity(apiUrl, stats, Void.class);
            log.info("✅ 관심사별 신규 기사 통계 전송 완료: {}개 관심사", stats.size());
        } catch (Exception e) {
            log.error("❌ API 서버 알림 요청 실패", e);
        }
    }
}
