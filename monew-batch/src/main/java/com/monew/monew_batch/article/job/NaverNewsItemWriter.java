package com.monew.monew_batch.article.job;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.entity.InterestArticles;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.article.repository.InterestArticleKeywordRepository;
import com.monew.monew_api.article.repository.InterestArticlesRepository;
import com.monew.monew_api.common.exception.article.ArticleNotFoundException;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.InterestKeyword;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleInterestPair;
import com.monew.monew_batch.article.repository.ArticleJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverNewsItemWriter implements ItemWriter<List<ArticleInterestPair>> {

    private final ArticleJdbcRepository articleJdbcRepository;
    private final ArticleRepository articleRepository;
    private final InterestArticlesRepository interestArticlesRepository;
    private final InterestArticleKeywordRepository interestArticleKeywordRepository;
    private final RestTemplate restTemplate;

    @Value("${monew.api.url}")
    private String monewApiUrl;

    @Override
    public void write(Chunk<? extends List<ArticleInterestPair>> chunk) {
        int total = 0, newCount = 0, linkedCount = 0, skippedCount = 0;

        // <관심사ID, 새롭게 연결된 기사 수> 집계용 맵
        Map<Long, Integer> newLinkCountsByInterestId = new HashMap<>();

        for (List<ArticleInterestPair> batch : chunk) {
            for (ArticleInterestPair pair : batch) {
                total++;
                Article article = pair.article();
                Interest interest = pair.interest();

                // 1. 기사 저장 및 복구 처리
                boolean isNew = handleInsertIgnore(article);
                if (isNew) newCount++;

                Article savedArticle = handleRestoreAndFind(article);

                // 2. 관심사·기사·키워드 관계 처리
                ProcessResult result = handleInterestAndKeywords(savedArticle, interest, newLinkCountsByInterestId);

                linkedCount += result.linkedCount();
                skippedCount += result.skippedCount();
            }
        }

        if (!newLinkCountsByInterestId.isEmpty()) {
            try {
                String apiUrl = monewApiUrl + "/api/internal/notifications/articles-registered";
                restTemplate.postForEntity(apiUrl, newLinkCountsByInterestId, Void.class);
                log.info("API 서버에 알림 생성 요청 완료: {}개 관심사", newLinkCountsByInterestId.size());
            } catch (Exception e) {
                log.error("API 서버 알림 생성 요청 실패");
            }
        }

        logSummary(total, newCount, linkedCount, skippedCount);
    }

    /**
     * JdbcTemplate 기반 insertIgnore 실행
     */
    private boolean handleInsertIgnore(Article article) {
        boolean isNew = articleJdbcRepository.insertIgnore(article);
        if (isNew) {
            log.info("🆕 신규 기사 저장: {}", article.getTitle());
        }
        return isNew;
    }

    /**
     * 삭제된 기사 복구 + DB 조회
     */
    private Article handleRestoreAndFind(Article article) {
        if (articleRepository.restoreIfDeleted(article.getSourceUrl()) > 0) {
            log.info("♻️ 복구된 기사: {}", article.getTitle());
        }

        return articleRepository.findBySourceUrl(article.getSourceUrl())
                .orElseThrow(ArticleNotFoundException::new);
    }

    /**
     * 관심사-기사 관계 및 키워드 연결 처리
     */
    private ProcessResult handleInterestAndKeywords(Article article, Interest interest,
                                                    Map<Long, Integer> newLinkCountsByInterestId) {
        int linkedCount = 0;
        int skippedCount = 0;

        // 1. 관심사-기사 연결 (InterestArticles)
        InterestArticles interestArticle =
                interestArticlesRepository.findByArticleAndInterest(article, interest)
                        .orElseGet(() -> {
                            InterestArticles newLink = new InterestArticles(article, interest);
                            interestArticlesRepository.save(newLink);
                            log.info("🔗 [{}] 관심사-기사 연결 완료: {}", interest.getName(), article.getTitle());

                            // 알림 이벤트 생성용 <관심사, 추가된 기사 개수> 처리
                            newLinkCountsByInterestId.put(interest.getId(),
                                    newLinkCountsByInterestId.getOrDefault(interest.getId(), 0) + 1);

                            return newLink;
                        });

        // 2. 관심사-키워드 연결 (InterestArticlesKeywords)
        for (InterestKeyword ik : interest.getKeywords()) {
            Keyword keyword = ik.getKeyword();
            int inserted = interestArticleKeywordRepository.insertIgnore(
                    interestArticle.getId(), keyword.getId()
            );

            if (inserted > 0) {
                linkedCount++;
                log.info("📎 [{}-{}] 연결 완료: {}", interest.getName(), keyword.getKeyword(), article.getTitle());
            } else {
                skippedCount++;
            }
        }

        return new ProcessResult(linkedCount, skippedCount);
    }

    /**
     * 결과 요약 로그
     */
    private void logSummary(int total, int newCount, int linkedCount, int skippedCount) {
        log.info("💾 Writer 결과 | 총: {} | 신규 기사: {} | 연결: {} | 스킵(중복): {}",
                total, newCount, linkedCount, skippedCount);
    }

    private record ProcessResult(int linkedCount, int skippedCount) {}
}