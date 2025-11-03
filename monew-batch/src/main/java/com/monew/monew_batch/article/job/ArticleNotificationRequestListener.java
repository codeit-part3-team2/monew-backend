package com.monew.monew_batch.article.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleNotificationRequestListener implements JobExecutionListener {

    private final RestTemplate restTemplate;

    @Value("${monew.api.url}")
    private String monewApiUrl;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> stats =
                (Map<Long, Integer>) jobExecution.getExecutionContext().get("newLinkCountsByInterestId");

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