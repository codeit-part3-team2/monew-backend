package com.monew.monew_batch.article.job;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import com.monew.monew_batch.article.properties.NaverApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverNewsItemProcessor implements ItemProcessor<Keyword, List<ArticleKeywordPair>> {

    private static final int DISPLAY_COUNT = 10;
    private static final String SORT_TYPE = "sim";
    private static final int REQUEST_DELAY_MS = 200;

    private final RestTemplate restTemplate;
    private final NaverApiProperties properties;

    @Override
    public List<ArticleKeywordPair> process(Keyword keyword) {
        String keywordText = keyword.getKeyword();
        log.info("'{}' 뉴스 수집 시작", keywordText);

        List<Map<String, Object>> items = fetchNewsItems(keywordText);
        if (items.isEmpty()) {
            log.warn("[{}] 뉴스 없음", keywordText);
            return Collections.emptyList();
        }

        List<ArticleKeywordPair> pairs = new ArrayList<>();
        for (Map<String, Object> item : items) {
            String title = cleanText((String) item.get("title"));
            String desc = cleanText((String) item.get("description"));
            String link = Optional.ofNullable((String) item.get("link")).orElse("");
            String pubDateStr = (String) item.get("pubDate");
            LocalDateTime publishDate = parsePublishDate(pubDateStr);

            Article article = new Article("Naver", link, title, publishDate, desc);
            pairs.add(new ArticleKeywordPair(article, keyword));
        }

        log.info("'{}' 뉴스 {}건 수집 완료", keywordText, pairs.size());

        try {
            Thread.sleep(REQUEST_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("뉴스 수집 중 인터럽트 발생", e);
        }

        return pairs;
    }

    private List<Map<String, Object>> fetchNewsItems(String keyword) {
        String uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .queryParam("query", keyword)
                .queryParam("display", DISPLAY_COUNT)
                .queryParam("sort", SORT_TYPE)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", properties.getClientId());
        headers.set("X-Naver-Client-Secret", properties.getClientSecret());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        Map<String, Object> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class).getBody();
        return (List<Map<String, Object>>) response.getOrDefault("items", Collections.emptyList());
    }

    private String cleanText(String text) {
        return Optional.ofNullable(text)
                .map(t -> t.replaceAll("<[^>]*>", ""))
                .orElse("");
    }

    private LocalDateTime parsePublishDate(String pubDateStr) {
        try {
            return ZonedDateTime.parse(pubDateStr, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

}