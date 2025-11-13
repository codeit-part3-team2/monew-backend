package com.monew.monew_batch.article.scheduler;

import com.monew.monew_batch.article.service.AricleBackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 뉴스 백업 스케줄러
 * - 매일 새벽 4시, S3 자동 백업
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class AricleBackupScheduler {

    private final AricleBackupService aricleBackupService;

    @Scheduled(cron = "0 20 4 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedRate = 600000) // 테스트용
    public void backupNews() {
        log.info("🗄 뉴스 백업 시작");
        aricleBackupService.backupAllArticles();
        log.info("🗃 뉴스 백업 완료");
    }
}
