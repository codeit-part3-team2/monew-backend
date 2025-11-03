package com.monew.monew_batch.article.job;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.entity.InterestArticles;
import com.monew.monew_api.article.repository.ArticleJdbcRepository;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.article.repository.InterestArticleKeywordRepository;
import com.monew.monew_api.article.repository.InterestArticlesRepository;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class NaverNewsItemWriter implements ItemWriter<List<ArticleKeywordPair>>, StepExecutionListener {

    private final ArticleJdbcRepository articleJdbcRepository;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final InterestArticlesRepository interestArticlesRepository;
    private final InterestArticleKeywordRepository interestArticleKeywordRepository;

    private final Map<Long, Integer> newLinkCountsByInterestId = new ConcurrentHashMap<>();

    @Override
    public void write(Chunk<? extends List<ArticleKeywordPair>> chunk) {
        int total = 0, newCount = 0, linkedCount = 0;

        for (List<ArticleKeywordPair> batch : chunk) {
            for (ArticleKeywordPair pair : batch) {
                total++;
                Article article = pair.article();
                Keyword keyword = pair.keyword();

                boolean isNew = articleJdbcRepository.insertIgnore(article);
                if (isNew) newCount++;

                Article savedArticle = articleRepository.findBySourceUrl(article.getSourceUrl())
                        .orElseThrow();

                List<Interest> relatedInterests = interestRepository.findAllByKeyword(keyword);
                for (Interest interest : relatedInterests) {
                    int insertedRow = interestArticlesRepository.insertIgnore(interest.getId(), savedArticle.getId());
                    if (insertedRow > 0) {
                        newLinkCountsByInterestId.merge(interest.getId(), 1, Integer::sum);
                    }

                    InterestArticles ia = interestArticlesRepository.findByArticleAndInterest(savedArticle, interest)
                            .orElseThrow();

                    interestArticleKeywordRepository.insertIgnore(ia.getId(), keyword.getId());
                    linkedCount++;
                }
            }
        }

        log.info("Writer 결과 | 총: {} | 신규 기사: {} | 연결: {}", total, newCount, linkedCount);
    }

    // 스텝이 모두 끝난 후 한 번만 JobExecutionContext에 데이터 저장
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Writer Step 완료 - 관심사별 누적 데이터: {}", newLinkCountsByInterestId);

        if (!newLinkCountsByInterestId.isEmpty()) {
            stepExecution.getJobExecution()
                    .getExecutionContext()
                    .put("newLinkCountsByInterestId", newLinkCountsByInterestId);
            log.info("JobExecutionContext에 최종 집계 데이터 저장 완료.");
        }

        return ExitStatus.COMPLETED;
    }
}
