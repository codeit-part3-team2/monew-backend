package com.monew.monew_batch.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monew.monew_api.article.dto.ArticleBackupData;
import com.monew.monew_batch.article.matric.ArticleBatchMetrics;
import com.monew.monew_batch.article.repository.ArticleBackupQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 뉴스 백업 서비스
 * - JPA fetch join 기반으로 조회된 기사/키워드를 S3에 JSON으로 백업
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AricleBackupService {

    private final ArticleBackupQueryRepository backupQueryRepository;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final ArticleBatchMetrics metrics;

    @Value("${aws.bucket}")
    private String bucketName;

    private static final String PREFIX = "backup/article_backup_";

    @Transactional(readOnly = true)
    public void backupAllArticles() {
        List<ArticleBackupData.ArticleData> articles = backupQueryRepository.findAllArticlesForBackup();

        if (articles.isEmpty()) {
            log.info("백업할 뉴스가 없습니다. (isDeleted = false)");
            return;
        }

        ArticleBackupData backupData = new ArticleBackupData();
        backupData.setBackupDate(LocalDateTime.now());
        backupData.setArticles(articles);

        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(backupData);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
            String key = PREFIX + timestamp + ".json";

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType("application/json")
                            .build(),
                    RequestBody.fromString(json, StandardCharsets.UTF_8)
            );

            log.info("✅ 뉴스 전체 백업 완료 | 총 {}건 | S3 Key = {}", articles.size(), key);
            metrics.recordBackup(true, articles.size());

        } catch (Exception e) {
            log.error("❌ 뉴스 백업 실패", e);
            metrics.recordBackup(false, 0);
            throw new RuntimeException("뉴스 백업 실패", e);
        }
    }
}
