package com.monew.monew_api.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monew.monew_api.article.dto.ArticleBackupData;
import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.repository.*;
import com.monew.monew_api.common.entity.BaseIdEntity;
import com.monew.monew_api.common.exception.article.ArticleNotFoundException;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_api.interest.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsRestoreService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final ArticleJdbcRepository articleJdbcRepository;
    private final KeywordRepository keywordRepository;
    private final InterestRepository interestRepository;
    private final InterestArticlesRepository interestArticlesRepository;
    private final InterestArticleKeywordRepository interestArticleKeywordRepository;

    @Value("${aws.bucket}")
    private String bucketName;

    private static final String PREFIX = "backup/article_backup_";
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");

    /** 메인 진입점 */
    @Transactional
    public void restoreArticles(LocalDateTime from, LocalDateTime to) {
        long start = System.currentTimeMillis();
        log.info("🗃 복원 시작: {} ~ {}", from, to);

        try {
            // 1. S3에서 파일 목록 가져오기
            List<String> fileKeys = getBackupFileKeys(from, to);
            if (fileKeys.isEmpty()) return;

            // 2. 여러 백업 파일 병합
            List<ArticleBackupData.ArticleData> mergedArticles = mergeBackupData(fileKeys);
            if (mergedArticles.isEmpty()) return;

            // 3. 이미 존재하는 기사 제외
            List<ArticleBackupData.ArticleData> newArticles = filterExistingArticles(mergedArticles);
            if (newArticles.isEmpty()) return;

            log.info("📰 신규 기사 {}건 복원 시도", newArticles.size());

            // 4. 기사 단위 복원
            int restored = 0, skipped = 0;

            for (ArticleBackupData.ArticleData data : newArticles) {
                boolean success = restoreSingleArticleExact(data);
                if (success) restored++;
                else skipped++;
            }

            log.info("✅ 복원 완료 | 성공: {}건, 스킵: {}건", restored, skipped);

        } finally {
            long end = System.currentTimeMillis();
            log.info("⏰ 복원 종료: 총 {}초 소요", (end - start) / 1000.0);
        }
    }

    /** 지정된 기간의 S3 백업 파일 목록 조회 */
    private List<String> getBackupFileKeys(LocalDateTime from, LocalDateTime to) {
        List<String> keys = s3Client.listObjectsV2(b -> b.bucket(bucketName).prefix("backup/"))
                .contents().stream()
                .map(S3Object::key)
                .filter(k -> k.startsWith(PREFIX))
                .filter(k -> {
                    LocalDateTime date = parseDateFromKey(k);
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .toList();

        if (keys.isEmpty()) log.info("📂 복원할 백업 파일이 없습니다.");
        return keys;
    }

    /** 파일명에서 날짜 추출 */
    private LocalDateTime parseDateFromKey(String key) {
        try {
            String datePart = key.replace(PREFIX, "").replace(".json", "");
            return LocalDateTime.parse(datePart, FILE_DATE_FORMAT);
        } catch (Exception e) {
            return LocalDateTime.MIN;
        }
    }

    /** 여러 백업 파일 병합 */
    private List<ArticleBackupData.ArticleData> mergeBackupData(List<String> keys) {
        Map<String, ArticleBackupData.ArticleData> merged = new LinkedHashMap<>();

        for (String key : keys) {
            try {
                String json = s3Client.getObjectAsBytes(b -> b.bucket(bucketName).key(key)).asUtf8String();
                ArticleBackupData backup = objectMapper.readValue(json, ArticleBackupData.class);
                backup.getArticles().forEach(a -> merged.putIfAbsent(a.getSourceUrl(), a));
            } catch (Exception e) {
                log.error("⚠️ 백업 파일 로드 실패: {}", key, e);
            }
        }

        if (merged.isEmpty()) log.info("📄 병합된 복원 대상이 없습니다.");
        return new ArrayList<>(merged.values());
    }

    /** 이미 존재하는 기사 제외 */
    private List<ArticleBackupData.ArticleData> filterExistingArticles(List<ArticleBackupData.ArticleData> articles) {
        Set<String> existingUrls = articleRepository.findAllSourceUrls();
        return articles.stream()
                .filter(a -> !existingUrls.contains(a.getSourceUrl()))
                .toList();
    }

    /** 기사 복원 (Writer 시점과 동일하되 insertIgnore 적용) */
    private boolean restoreSingleArticleExact(ArticleBackupData.ArticleData data) {
        try {
            boolean inserted = articleJdbcRepository.insertIgnore(data.toEntity());
            if (!inserted) return false;

            // insertIgnore은 영속성 컨텍스으에 반영안됌 -> id로 조회 못함
            Article article = articleRepository.findBySourceUrl(data.getSourceUrl())
                    .orElseThrow(ArticleNotFoundException::new);

            List<String> keywords = data.getKeywords();
            for (String keywordName : keywords) {
                keywordRepository.findByKeyword(keywordName).ifPresent(keyword -> {
                    List<Interest> interests = interestRepository.findAllByKeyword(keyword);
                    for (Interest interest : interests) {
                        int result = interestArticlesRepository.insertIgnore(interest.getId(), article.getId());
                        if (result > 0) {
                            log.info("🔗 [{}] 관심사-기사 연결 완료: {}", interest.getName(), article.getTitle());
                        }

                        interestArticleKeywordRepository.insertIgnore(
                                interestArticlesRepository.findByArticleAndInterest(article, interest)
                                        .map(BaseIdEntity::getId)
                                        .orElseThrow(),
                                keyword.getId()
                        );
                    }
                });
            }
            return true;
        } catch (Exception e) {
            log.error("⚠️ 기사 [{}] 복원 실패: {}", data.getTitle(), e.getMessage());
            return false;
        }
    }
}