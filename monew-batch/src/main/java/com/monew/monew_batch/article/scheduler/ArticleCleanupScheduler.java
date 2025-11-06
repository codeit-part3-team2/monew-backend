package com.monew.monew_batch.article.scheduler;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_batch.article.matric.ArticleBatchMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCleanupScheduler {

    private final ArticleRepository articleRepository;
    private final ArticleBatchMetrics metrics;

    /**
     * 매일 새벽 4시에 is_deleted = true인 뉴스들을 물리 삭제
     */
    @Transactional
    @Scheduled(cron = "0 10 4 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedRate = 600000) // 테스트용
    public void deleteSoftDeletedArticles() {
        log.info("🧹 [ArticleCleanupScheduler] 논리 삭제된 뉴스 정리 시작");
        List<Article> deletedArticles = articleRepository.findAllByIsDeletedTrue();
        if (deletedArticles.isEmpty()) {
            log.info("✅ 삭제할 뉴스 없음");
            return;
        }

        int total = deletedArticles.size();
        articleRepository.deleteAll(deletedArticles);

        metrics.recordCleanup(total);
        log.info("🗑 물리 삭제 완료 | 총 {}건 (FK CASCADE 포함)", total);
    }

}
