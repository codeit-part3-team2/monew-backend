package com.monew.monew_batch.article.job;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.entity.InterestArticles;
import com.monew.monew_api.article.repository.ArticleJdbcRepository;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.article.repository.InterestArticleKeywordRepository;
import com.monew.monew_api.article.repository.InterestArticlesRepository;
import com.monew.monew_api.common.exception.article.ArticleNotFoundException;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.InterestKeyword;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
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
public class NaverNewsItemWriter implements ItemWriter<List<ArticleKeywordPair>> {

    private final ArticleJdbcRepository articleJdbcRepository;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final InterestArticlesRepository interestArticlesRepository;
    private final InterestArticleKeywordRepository interestArticleKeywordRepository;
    private final RestTemplate restTemplate;

    @Value("${monew.api.url}")
    private String monewApiUrl;

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
                    interestArticlesRepository.insertIgnore(interest.getId(), savedArticle.getId());

                    InterestArticles ia = interestArticlesRepository.findByArticleAndInterest(savedArticle, interest)
                            .orElseThrow();

                    interestArticleKeywordRepository.insertIgnore(ia.getId(), keyword.getId());
                    linkedCount++;
                }
            }
        }

        log.info("Writer 결과 | 총: {} | 신규 기사: {} | 연결: {}", total, newCount, linkedCount);
    }

}